package tds.support.tool.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import tds.support.tool.security.permission.PermissionService;

import java.util.Collection;
import java.util.Map;

/**
 * Wrap the basic permission service to cache the results since role/permissions don't change often.
 */
@Primary
@Service
class CachingPermissionService implements PermissionService {

    private final PermissionService permissionService;

    public CachingPermissionService(@Qualifier("basicPermissionService") final PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Cacheable("permissionsByRole")
    public Map<String, Collection<String>> getPermissionsByRole() throws UnauthorizedUserException {
        return permissionService.getPermissionsByRole();
    }
}

