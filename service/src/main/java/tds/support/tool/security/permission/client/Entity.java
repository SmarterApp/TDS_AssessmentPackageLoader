package tds.support.tool.security.permission.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Permission REST service response model
 * This class represents an entity for which permissions can be granted.
 * (e.g. STATE, DISTRICT, INSTITUTION)
 */
public class Entity implements Serializable {

    private String entity;
    private String description;

    @JsonCreator
    public Entity(
            @JsonProperty("entity") String entity,
            @JsonProperty("description") String description) {
        this.entity = entity;
        this.description = description;
    }

    public String getEntity() {
        return entity;
    }

    public String getDescription() {
        return description;
    }

}