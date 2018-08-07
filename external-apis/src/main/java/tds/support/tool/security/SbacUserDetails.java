package tds.support.tool.security;

import tds.support.tool.security.common.TenancyChain;
import tds.support.tool.security.model.PermissionScope;

/**
 * Provides SBAC user information.
 * <p>
 * Instead of extending UserDetails which introduces fields we don't care about, this interface exposes username.
 * Concrete implementations will probably extend User, inheriting the getUsername() method.
 */
public interface SbacUserDetails {

    /**
     * @return username of the user
     */
    String getUsername();

    /**
     * @return UID of the user
     */
    String getUid();

    /**
     * @return sbacUUID of the user
     */
    String getSbacUuid();

    /**
     * @return tenancy chain of the user
     */
    TenancyChain getTenancyChain();

    /**
     * @param permission permission id to lookup, e.g. "GROUP_WRITE"
     * @return permission scope for given permission, may be EMPTY, won't be null
     */
    PermissionScope getPermissionScope(String permission);
}
