package tds.support.tool.security;

import org.springframework.security.core.GrantedAuthority;
import tds.support.tool.security.common.Permission;
import tds.support.tool.security.common.TenancyChain;
import tds.support.tool.security.model.Grant;

import java.util.List;
import java.util.Set;

/**
 * This service knows all about roles, permissions and spring security authorities.
 * Some mixing of concerns here, and the name is a bit misleading - deal with it.
 */
public interface AuthorityService {

    /**
     * Extract authorities from roles granted by a tenancy chain.<br/>
     * Only roles and permissions associated with this component and client/state
     * are converted, other roles/permissions in the grants are quietly ignored.
     *
     * @param tenancyChain tenancy chain with grants
     * @return set of authorities granted by the tenancy chain
     */
    Set<GrantedAuthority> getAuthorities(TenancyChain tenancyChain);

    /**
     * Extract permissions from roles granted by a tenancy chain.<br/>
     * Only roles and permissions associated with this component and client/state
     * are converted, other roles/permissions in the grants are quietly ignored.
     *
     * @param tenancyChain tenancy chain with grants
     * @return set of permissions granted by the tenancy chain.
     */
    Set<Permission> getPermissions(TenancyChain tenancyChain);

    /**
     * From a tenancy chain, extract just those grants for this component and client/state.
     *
     * @param tenancyChain tenancy chain representing all grants
     * @return list of pertinent grants
     */
    List<Grant> getGrants(TenancyChain tenancyChain);
}