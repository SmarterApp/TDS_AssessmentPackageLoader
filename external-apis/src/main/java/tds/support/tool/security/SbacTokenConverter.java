package tds.support.tool.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import tds.support.tool.security.common.TenancyChain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * A {@link AccessTokenConverter} that is aware of SBAC fields. Used by {@link ForgeRockTokenServices}.
 * Uses the {@link AuthorityService} to convert tenancy chain grants to spring security authorities.
 * Note that the auth request will have the "expires_in" value offset from now as a request param;
 * this is used by {@link ForgeRockTokenServices.ExpiringAuthenticationCache}.
 *
 * This is not annotated as a component so that a stub version can be created for dev/tests.
 * @see SecurityConfigurer
 */
@Component
class SbacTokenConverter implements AccessTokenConverter {
    private static final String EXPIRES_IN = "expires_in";
    private static final int DEFAULT_EXPIRES_IN = 3600;

    private final AuthorityService authorityService;

    @Autowired
    SbacTokenConverter(final AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public Map<String, ?> convertAccessToken(final OAuth2AccessToken token, final OAuth2Authentication authentication) {
        // this method will only be called in response to GET check_token and
        // some JWT handling neither of which is supported
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2AccessToken extractAccessToken(final String value, final Map<String, ?> map) {
        // this method will only be called in response to GET check_token which isn't supported
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2Authentication extractAuthentication(final Map<String, ?> map) {
        final String username = (String) map.get("mail");
        final List<GrantedAuthority> authorities = newArrayList();
        final Authentication authentication;
        if (username == null || username.equals("")) {
            authentication = null;
        } else {
            final TenancyChain tenancyChain = TenancyChain.fromString((String) map.get("sbacTenancyChain"));
            authorities.addAll(authorityService.getAuthorities(tenancyChain));

            final SbacUser user = SbacUser.builder()
                    .username(username)
                    .authorities(newArrayList(authorities))
                    .uid((String) map.get("uid"))
                    .sbacUuid((String) map.get("sbacUUID"))
                    .tenancyChain(tenancyChain)
                    .permissions(authorityService.getPermissions(tenancyChain))
                    .build();
            authentication = new PreAuthenticatedAuthenticationToken(user, null, authorities);
        }

        final Map<String, String> params = newHashMap();
        params.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
        params.put(EXPIRES_IN, extractExpirationAsTime(map).toString());
        final OAuth2Request request = new OAuth2Request(params, null, authorities, true, extractScope(map), null, null, null, null);

        return new OAuth2Authentication(request, authentication);
    }

    private static Set<String> extractScope(final Map<String, ?> map) {
        return map.containsKey(SCOPE) ? newHashSet((List<String>) map.get(SCOPE)) : null;
    }

    private static Long extractExpirationAsTime(final Map<String, ?> map) {
        final int expiresInSeconds = map.containsKey(EXPIRES_IN) ? (Integer) map.get(EXPIRES_IN) : DEFAULT_EXPIRES_IN;
        return System.currentTimeMillis() + 1000 * expiresInSeconds;
    }
}
