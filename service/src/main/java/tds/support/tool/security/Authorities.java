package tds.support.tool.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Utility for dealing with spring {@link GrantedAuthority}
 */
public final class Authorities {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String PERMISSION_PREFIX = "PERM_";

    /**
     * Creates and returns a role authority for the given name.
     * If a name of "THING_READ" is provided a {@link GrantedAuthority} with id "ROLE_THING_READ" will be returned
     * @param name The name of the role
     * @return role
     */
    public static GrantedAuthority createRole(final String name) {
        return new SimpleGrantedAuthority(ROLE_PREFIX + formatAuthorityName(name));
    }

    /**
     * Creates and returns a permission authority for the given name.
     * If a name of "THING_READ" is provided a {@link GrantedAuthority} with id "PERM_THING_READ" will be returned
     * @param name The name of the permission
     * @return permission.
     */
    public static GrantedAuthority createPermission(final String name) {
        return new SimpleGrantedAuthority(PERMISSION_PREFIX + formatAuthorityName(name));
    }

    /**
     * Formats formats role or permission name in the spring standard format
     * of capitol letter words separated by underscores
     * @param name The name of the role or permission
     * @return formatted name. (e.g. "Thing Read" becomes "THING_READ")
     */
    private static String formatAuthorityName(final String name) {
        if (name == null) {
            return "";
        }
        return name.trim().replaceAll("\\s+", "_").toUpperCase();
    }

}