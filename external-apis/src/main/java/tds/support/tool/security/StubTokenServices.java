package tds.support.tool.security;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.GRANT_TYPE;

/**
 * This stub implementation of token services expects the access token to be of a special form
 * that contains the client id and optional username and tenancy chain. Format of token is:<pre>
 * client-id;username;tenancy-chain</pre>
 * Some examples:<pre>
 * sbac;alice;|SBAC|ASMTDATALOAD|CLIENT|SBAC||||||||||||||
 * sbac;bob;
 * sbac;; </pre>
 * Token for alice grants ASMTDATALOAD authority.<br/>
 * Token for bob is good for authentication but grants no authorities.<br/>
 * The last token is a client token good for authentication but having no user (and thus no authorities).<br/>
 * <p>
 * In case it isn't obvious: <b>this is for testing and local development ONLY</b>.
 * </p>
 */
public class StubTokenServices implements ResourceServerTokenServices {
    private static final String StubTokenDelimiter = ";";

    private final AccessTokenConverter tokenConverter;
    private final LoadingCache<String, OAuth2Authentication> cache;

    public StubTokenServices(final AccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
        this.cache = new ExpiringAuthenticationCache(key -> tokenConverter.extractAuthentication(tokenToMap(key)));
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
        return tokenConverter.extractAccessToken(accessToken, tokenToMap(accessToken));
    }

    /**
     * Converts a stub access token to a map that corresponds to what the token converter is expecting.
     *
     * @param accessToken stub access token
     * @return map
     */
    private static Map<String, String> tokenToMap(final String accessToken) {
        final Map<String, String> map = newHashMap();
        final String[] values = accessToken.split(StubTokenDelimiter, -3);
        if (values.length != 3) {
            throw new InvalidTokenException("invalid stub token: " + accessToken);
        }
        map.put(GRANT_TYPE, "stub");
        map.put("mail", values[1]);
        map.put("sbacTenancyChain", values[2]);
        return map;
    }
}
