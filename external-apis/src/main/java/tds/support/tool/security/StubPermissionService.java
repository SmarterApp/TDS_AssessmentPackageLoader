package tds.support.tool.security;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import tds.support.tool.security.permission.PermissionService;

import java.util.Collection;
import java.util.Map;

/**
 * A stub implementation of {@link PermissionService} that just returns roles/permissions needed
 * to make testing easier. Currently returns ASMTDATALOAD -> {}.
 */
class StubPermissionService implements PermissionService {

    private final Map<String, Collection<String>> permissionsByRole =
            ImmutableMap.of(Authorities.DataLoadRole, ImmutableList.of(),
                    "GROUP_ADMIN", ImmutableList.of(Authorities.GroupWritePermission, "GROUP_READ"));

    @Override
    public Map<String, Collection<String>> getPermissionsByRole() {
        return permissionsByRole;
    }
}
