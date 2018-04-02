package tds.support.tool.security.permission;

import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import java.util.Collection;
import java.util.Map;

public interface PermissionService {
    Map<String, Collection<String>> getPermissionsByRole() throws UnauthorizedUserException;
}
