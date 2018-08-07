package tds.support.tool.security.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import tds.support.tool.security.permission.client.*;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Service
public class PermissionServiceImpl implements PermissionService {
    /**
     * Used to integrate with the permission service
     */
    private PermissionWebServiceClient client;

    /**
     * The component for which permission information will be looked up
     */
    private static final String SUPPORT_TOOL_COMPONENT_NAME = "Support Tool";

    @Autowired
    public PermissionServiceImpl(final PermissionWebServiceClient client) {
        this.client = checkNotNull(client);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Collection<String>> getPermissionsByRole() throws UnauthorizedUserException {

        final Response<Role> response = client.getRole(SUPPORT_TOOL_COMPONENT_NAME);

        if (response.getStatus() == null || !response.getStatus().equals(Response.Status.SUCCESS)) {
            throw new UnauthorizedUserException(String.format(
                    "Error getting roles and permissions for component \"%s\". Server responded with message: \"%s\"",
                    SUPPORT_TOOL_COMPONENT_NAME, response.getMessage()));
        }

        final Map<String, Collection<String>> permissionsByRole = newHashMap();
        for (Role role : response.getValue()) {
            final Collection<String> permissions = newHashSet();
            for (Component mapping : role.getMappings()) {
                for (Permission permission : mapping.getPermissions()) {
                    permissions.add(permission.getName());
                }
            }
            permissionsByRole.put(role.getRole(), permissions);
        }
        return permissionsByRole;
    }
}
