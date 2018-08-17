package tds.support.tool.security.permission.client;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Permission REST service response model
 * This class represents a collection of permissions and the component to which they belong.
 */
public class Component implements Serializable {

    private String component;
    private List<Permission> permissions;

    @JsonCreator
    public Component(
            @JsonProperty("component") String component,
            @JsonProperty("permissions") List<Permission> permissions) {
        this.component = component;
        this.permissions = permissions;
    }

    public String getComponent() {
        return component;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

}