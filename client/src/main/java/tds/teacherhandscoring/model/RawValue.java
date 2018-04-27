package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = RawValueSerializer.class)
@JsonDeserialize(using = RawValueDeserializer.class)
public class RawValue {

    public final String value;

    public RawValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}