package tds.support.tool.security;

public class Authorities {
    public final static String DataLoadRole = "ASMTDATALOAD";
    public final static String DataLoadAuthority = roleToAuthorityName(DataLoadRole);

    public final static String GroupWritePermission = "GROUP_WRITE";
    public final static String GroupWriteAuthority = permissionToAuthorityName(GroupWritePermission);

    static String roleToAuthorityName(final String role) {
        return "ROLE_" + role;
    }

    static String permissionToAuthorityName(final String permission) {
        return "PERM_" + permission;
    }
}
