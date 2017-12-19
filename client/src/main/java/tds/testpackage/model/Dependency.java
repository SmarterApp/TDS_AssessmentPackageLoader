package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_Dependency.Builder.class)
public abstract class Dependency {
    public abstract String getIfToolType();
    public abstract String getIfToolCode();
    public abstract boolean isEnabled();

    public static Builder builder() {
        return new AutoValue_Dependency.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIfToolType(String newIfToolType);

        public abstract Builder setIfToolCode(String newIfToolCode);

        public abstract Builder setEnabled(boolean newEnabled);

        public abstract Dependency build();
    }
}
