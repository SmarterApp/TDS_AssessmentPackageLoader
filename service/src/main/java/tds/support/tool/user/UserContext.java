package tds.support.tool.user;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class UserContext {
    private String firstName;
    private String lastName;
    private Set<String> permissions;

    private UserContext(){}

    /**
     * @return the user's given name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the user's family name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return all statewide permission IDs of the user
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String firstName;
        private String lastName;
        private Set<String> permissions;

        public Builder firstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder permissions(final Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public UserContext build() {
            final UserContext context = new UserContext();
            context.firstName = firstName;
            context.lastName = lastName;
            context.permissions = permissions != null ? ImmutableSet.copyOf(permissions) : ImmutableSet.of();
            return context;
        }

    }
}
