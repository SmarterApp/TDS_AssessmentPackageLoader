package tds.support.tool.security.permission.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Permission REST service response model
 * This class represents a role and contains the permissions and allowed entities associated with that role.
 */
public class Role implements Serializable {

    private String role;
    private List<Component> mappings;
    private List<Entity> allowableEntities;

    @JsonCreator
    public Role(
            @JsonProperty("role") String role,
            @JsonProperty("mappings") List<Component> mappings,
            @JsonProperty("allowableEntities") List<Entity> allowableEntities) {
        this.role = role;
        this.mappings = mappings;
        this.allowableEntities = allowableEntities;
    }

    public String getRole() {
        return role;
    }

    public List<Component> getMappings() {
        return mappings;
    }

    public List<Entity> getAllowableEntities() {
        return allowableEntities;
    }

}