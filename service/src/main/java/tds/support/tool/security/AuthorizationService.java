package tds.support.tool.security;

import org.springframework.security.core.GrantedAuthority;
import tds.support.tool.security.model.Grant;
import tds.support.tool.security.model.Permission;

import java.util.Collection;
import java.util.Set;

/**
 * Responsible for computing users' grants, roles and permissions
 */
public interface AuthorizationService {

    /**
     * Computes role grants from encoded tenancy chain grants provided by ART.
     * This method guarantees non-repeating entries, and ignores redundant entries.
     * An entry is considered redundant when it is equal to another or contained within another entry.
     *
     * @param encodedGrants an array of encoded grant strings provided by ART as-is
     * @return immutable set of unique role-specific grants
     */
    Set<Grant> getGrants(String[] encodedGrants);

    /**
     * Computes permissions from grants
     *
     * @param grants the grants for which to compute the permissions
     * @return immutable set of permissions
     */
    Set<Permission> getPermissions(Collection<Grant> grants);

    /**
     * Computes authorities from grants. This is for integrating with spring security
     *
     * @param grants the grants for which to compute the authorities
     * @return immutable set of authorities
     */
    Set<GrantedAuthority> getAuthorities(Collection<Grant> grants);

}