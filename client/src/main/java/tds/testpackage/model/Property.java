package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_Property.Builder.class)
public abstract class Property {
    public abstract String getName();
    public abstract String getValue();

    public static Builder builder() {
        return new AutoValue_Property.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setValue(String newValue);

        public abstract Property build();
    }
}
