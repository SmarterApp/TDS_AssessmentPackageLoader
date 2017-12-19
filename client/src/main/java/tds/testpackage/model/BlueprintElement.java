package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_BlueprintElement.Builder.class)
public abstract class BlueprintElement {
    public abstract String getId();
    public abstract String getType();
    public abstract Optional<Scoring> getScoring();
    @Nullable
    public abstract List<BlueprintElement> getBlueprintElements();

    public static Builder builder() {
        return new AutoValue_BlueprintElement.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setType(String newType);

        @JacksonXmlProperty(localName = "Scoring")
        public abstract Builder setScoring(Optional<Scoring> newScoring);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "BlueprintElement")
        public abstract Builder setBlueprintElements(List<BlueprintElement> newBlueprintElements);

        public abstract BlueprintElement build();
    }
}
