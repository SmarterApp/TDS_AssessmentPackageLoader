package tds.support.tool.security;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.ACCESS_TOKEN;

/**
 * An implementation of {@link ResourceServerTokenServices} backed by ForgeRock's OpenAM, which doesn't provide
 * the POST /oauth2/check_token end-point. Instead it has GET /oauth2/tokeninfo. And it doesn't require client info.
 *
 * @see SecurityConfigurer
 * @see SbacTokenConverter
 */
class ForgeRockTokenServices implements ResourceServerTokenServices {
    private static final Logger logger = LoggerFactory.getLogger(ForgeRockTokenServices.class);

    private String tokenInfoUrl;
    private RestOperations restTemplate;
    private final AccessTokenConverter tokenConverter;
    private final LoadingCache<String, OAuth2Authentication> cache;

    public ForgeRockTokenServices(final AccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
        this.restTemplate = new RestTemplate();
        this.cache = new ExpiringAuthenticationCache(key -> tokenConverter.extractAuthentication(getTokenInfo(key)));
    }

    public void setRestTemplate(final RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setTokenInfoUrl(final String tokenInfoUrl) {
        this.tokenInfoUrl = tokenInfoUrl;
    }

    @Override
    public OAuth2Authentication loadAuthentication(final String accessToken) throws AuthenticationException, InvalidTokenException {
        try {
            return cache.get(accessToken);
        } catch (final ExecutionException | UncheckedExecutionException e) {
            // propagate the cause if we can, otherwise wrap in generic auth exception
            throw e.getCause() instanceof RuntimeException ? (RuntimeException)e.getCause()
                    : new AuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(final String accessToken) {
        return tokenConverter.extractAccessToken(accessToken, getTokenInfo(accessToken));
    }

    private Map<String, Object> getTokenInfo(final String token) {
        final Map<String,String> params = newHashMap();
        params.put(ACCESS_TOKEN, token);
        final Map result;
        try {
            result = restTemplate.getForObject(tokenInfoUrl, Map.class, params);
        } catch (final RestClientException e) {
            logger.debug("getTokenInfo exception: {}", e.getMessage());
            throw new InvalidTokenException(token + ": " + e.getMessage());
        }

        if (result.containsKey("error")) {
            logger.debug("getTokenInfo returned error: {}", result.get("error"));
            throw new InvalidTokenException(token + ": " + result.get("error"));
        }

        return (Map<String, Object>) result;
    }
}
