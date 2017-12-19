package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Value.Builder.class)
public abstract class Value {
    public abstract Optional<String> getIndex();
    public abstract String getValue();

    public static Builder builder() {
        return new AutoValue_Value.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIndex(Optional<String> newIndex);

        public abstract Builder setValue(String newValue);

        public abstract Value build();
    }
}
