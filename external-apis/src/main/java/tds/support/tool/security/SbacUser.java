package tds.support.tool.security;

import com.google.common.collect.ImmutableList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import tds.support.tool.security.common.Permission;
import tds.support.tool.security.common.TenancyChain;
import tds.support.tool.security.model.PermissionScope;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * A user with SBAC details.
 */
public class SbacUser extends User implements SbacUserDetails {
    private final String uid;
    private final String sbacUuid;
    private final TenancyChain tenancyChain;
    private final List<Permission> permissions;

    private SbacUser(final String username,
                     final String password,
                     final Collection<? extends GrantedAuthority> authorities,
                     final String uid,
                     final String sbacUuid,
                     final TenancyChain tenancyChain,
                     final Collection<Permission> permissions) {
        super(username, password, authorities);
        this.uid = uid;
        this.sbacUuid = sbacUuid;
        this.tenancyChain = tenancyChain;
        this.permissions = ImmutableList.copyOf(permissions);
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getSbacUuid() {
        return sbacUuid;
    }

    @Override
    public TenancyChain getTenancyChain() {
        return tenancyChain;
    }

    @Override
    public PermissionScope getPermissionScope(final String id) {
        for (final Permission permission : permissions) {
            if (permission.getId().equals(id)) return permission.getScope();
        }
        return PermissionScope.EMPTY;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public Builder copy() {
        return new Builder().copy(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password = "N/A";
        private List<GrantedAuthority> authorities = newArrayList();
        private String uid;
        private String sbacUuid;
        private TenancyChain tenancyChain;
        private List<Permission> permissions = newArrayList();

        public SbacUser build() {
            return new SbacUser(username, password, authorities, uid, sbacUuid, tenancyChain, permissions);
        }

        public Builder copy(final SbacUser user) {
            username = user.getUsername();
            password = user.getPassword();
            authorities.addAll(user.getAuthorities());
            uid = user.getUid();
            sbacUuid = user.getSbacUuid();
            tenancyChain = user.getTenancyChain();
            permissions.addAll(user.permissions);
            return this;
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder authorities(final Collection<? extends GrantedAuthority> authorities) {
            this.authorities = newArrayList(authorities);
            return this;
        }

        public Builder authority(final GrantedAuthority authority) {
            this.authorities.add(authority);
            return this;
        }

        public Builder uid(final String uid) {
            this.uid = uid;
            return this;
        }

        public Builder sbacUuid(final String sbacUuid) {
            this.sbacUuid = sbacUuid;
            return this;
        }

        public Builder tenancyChain(final TenancyChain tenancyChain) {
            this.tenancyChain = tenancyChain;
            return this;
        }

        public Builder tenancyChain(final String value) {
            return this.tenancyChain(TenancyChain.fromString(value));
        }

        public Builder permissions(final Collection<Permission> permissions) {
            this.permissions = newArrayList(permissions);
            return this;
        }

        public Builder permission(final Permission permission) {
            this.permissions.add(permission);
            return this;
        }
    }
}
