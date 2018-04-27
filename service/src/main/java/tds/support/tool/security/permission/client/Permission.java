package tds.support.tool.security.permission.client;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Scoped permission used to limit database results
 */
public class Permission implements Serializable {

    private String name;

    @JsonCreator
    public Permission(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}