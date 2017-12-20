package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_BlueprintReference.Builder.class)
public abstract class BlueprintReference {
    public abstract String getIdRef();

    public static Builder builder() {
        return new AutoValue_BlueprintReference.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIdRef(String newIdRef);

        public abstract BlueprintReference build();
    }
}
