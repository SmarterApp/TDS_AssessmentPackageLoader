package tds.support.tool.security.permission;

import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import java.util.Collection;
import java.util.Map;

/**
 * An interface for a service for fetching permission and authorization related data
 */
public interface PermissionService {
    /**
     * Obtains a map of roles and permissions for the support tool component
     *
     * @return A map containing all roles and permissions for the user
     * @throws UnauthorizedUserException
     */
    Map<String, Collection<String>> getPermissionsByRole() throws UnauthorizedUserException;
}
