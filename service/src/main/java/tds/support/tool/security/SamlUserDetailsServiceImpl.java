package tds.support.tool.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import tds.support.tool.security.model.Grant;
import tds.support.tool.security.model.User;

import java.util.Set;


@Service
public class SamlUserDetailsServiceImpl implements SAMLUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SamlUserDetailsServiceImpl.class);
    private static final String REDACTED = "[REDACTED]";
//    private final AuthorizationService authorizationService;


    public Object loadUserBySAML(final SAMLCredential credential) throws UsernameNotFoundException {

        final String username = credential.getNameID().getValue();
        final String id = credential.getAttributeAsString("sbacUUID");
        final String firstName = credential.getAttributeAsString("givenName");
        final String lastName = credential.getAttributeAsString("sn");
        final String email = credential.getAttributeAsString("mail");
        final String[] encodedGrants = credential.getAttributeAsStringArray("sbacTenancyChain");

//        final Set<Grant> grants = authorizationService.getGrants(encodedGrants);

        // Secures the application by returning a user without any permissions, authorities, or groups.
//        if (grants.isEmpty()) {
//            final String message = String.format("Grants do not permit access to tenant for user \"%s\"", username);
//            logger.warn(message);
//
//            return User.builder()
//                    .id(id)
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .email(email)
//                    .username(username)
//                    .password(REDACTED)
//                    .build();
//        }

//        final Set<Permission> permissions = authorizationService.getPermissions(grants);
//        final Set<GrantedAuthority> authorities = authorizationService.getAuthorities(grants);

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
                .build();
    }

}