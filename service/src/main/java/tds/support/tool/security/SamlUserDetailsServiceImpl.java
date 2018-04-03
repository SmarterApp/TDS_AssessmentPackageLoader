package tds.support.tool.security;


import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import tds.support.tool.security.model.Grant;
import tds.support.tool.security.model.User;
import tds.support.tool.security.permission.PermissionService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


@Service
public class SamlUserDetailsServiceImpl implements SAMLUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SamlUserDetailsServiceImpl.class);
    private static final String REDACTED = "[REDACTED]";
    private final PermissionService permissionService;

    @Autowired
    public SamlUserDetailsServiceImpl(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    public Object loadUserBySAML(final SAMLCredential credential) throws UsernameNotFoundException {

        final String username = credential.getNameID().getValue();
        final String id = credential.getAttributeAsString("sbacUUID");
        final String firstName = credential.getAttributeAsString("givenName");
        final String lastName = credential.getAttributeAsString("sn");
        final String email = credential.getAttributeAsString("mail");
        final String[] encodedGrants = credential.getAttributeAsStringArray("sbacTenancyChain");

        final Set<Grant> grants = getGrants(encodedGrants);

        // Secures the application by returning a user without any permissions, authorities, or groups.
        if (grants.isEmpty()) {
            final String message = String.format("Grants do not permit access to tenant for user \"%s\"", username);
            logger.warn(message);

            return User.builder()
                    .id(id)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .username(username)
                    .password(REDACTED)
                    .build();
        }

        final Set<GrantedAuthority> authorities = getGrantedAuthorities(grants);

        return User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .username(username)
                .password(REDACTED)
                .enabled(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .authorities(authorities)
                .build();
    }

    private Set<GrantedAuthority> getGrantedAuthorities(final Set<Grant> grants) {
        final Map<String, Collection<String>> permissionsByRole = permissionService.getPermissionsByRole();
        final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();

        for (Grant grant : grants) {
            if (permissionsByRole.containsKey(grant.getRole())) {
                authorities.add(Authorities.createRole(grant.getRole()));
                final Collection<String> permissionIds = permissionsByRole.get(grant.getRole());
                if (permissionIds != null) {
                    for (String permission : permissionIds) {
                        authorities.add(Authorities.createPermission(permission));
                    }
                }
            }
        }
        return authorities.build();
    }

    private Set<Grant> getGrants(final String[] encodedGrants) {
        final ImmutableSet.Builder<Grant> grants = ImmutableSet.builder();
        for (final String encodedGrant : encodedGrants) {
            try {
                grants.add(Grant.fromString(encodedGrant));
            } catch (final IllegalArgumentException exception) {
                // in a well-behaved and properly configured ecosystem this should never happen
                // so log a WARN level message so the log may be monitored
                logger.warn("invalid grant: {}", encodedGrant);
            }
        }
        return grants.build();
    }

}