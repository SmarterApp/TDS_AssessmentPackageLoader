package tds.support.tool.security.model;


import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Scoped permission used to limit database results
 */
public class Permission implements Serializable {

    /**
     * The permission ID
     */
    private final String id;

    /**
     * The scope or access level the permission is granted for
     */
    private final PermissionScope scope;

    /**
     * Default constructor for serialization
     */
    private Permission() {
        this("UNKNOWN", PermissionScope.EMPTY);
    }

    public Permission(
            @NotNull final String id,
            @NotNull final PermissionScope scope) {
        this.id = checkNotNull(id, "id must not be null");
        this.scope = checkNotNull(scope, "scope must not be null");
    }


    public String getId() {
        return id;
    }

    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("scope", getScope())
                .toString();
    }
}