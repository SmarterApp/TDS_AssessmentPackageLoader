package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = DimensionsSerializer.class)
@JsonDeserialize(using = DimensionsDeserializer.class)
public class Dimensions {

    public final String value;

    public Dimensions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}