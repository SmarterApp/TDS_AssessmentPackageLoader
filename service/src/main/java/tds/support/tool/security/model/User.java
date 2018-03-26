package tds.support.tool.security.model;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class User extends org.springframework.security.core.userdetails.User {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Map<String, Permission> permissionsById;

    private User(final String username,
                 final String password,
                 final boolean enabled,
                 final boolean accountNonExpired,
                 final boolean credentialsNonExpired,
                 final boolean accountNonLocked,
                 final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }


    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Map<String, Permission> getPermissionsById() {
        return permissionsById;
    }

    public PermissionScope getPermissionScopeByPermissionId(final String id) {
        final Permission permission = getPermissionsById().get(id);
        if (permission == null) {
            return PermissionScope.EMPTY;
        }
        return permission.getScope();
    }

    /**
     * Builder for User
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private Map<String, Permission> permissionsById;
        private String password = "[REDACTED]";
        private String username;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean enabled;

        public User build() {
            final Collection<? extends GrantedAuthority> auth = authorities != null ? ImmutableSet.copyOf(authorities) : ImmutableSet.of();
            final User user = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, auth);
            user.id = checkNotNull(id, "id must not be null");
            user.email = email;
            user.firstName = firstName;
            user.lastName = lastName;
            user.permissionsById = permissionsById != null ? ImmutableMap.copyOf(permissionsById) : ImmutableMap.of();
            return user;
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder firstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder permissionsById(final Map<String, Permission> permissionsById) {
            this.permissionsById = permissionsById;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder authorities(final Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder accountNonExpired(final boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public Builder accountNonLocked(final boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public Builder credentialsNonExpired(final boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public Builder enabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

    }
}