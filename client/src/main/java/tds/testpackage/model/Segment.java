package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Segment.Builder.class)
public abstract class Segment {
    public abstract String getId();

    public abstract Optional<Integer> getPosition();

    public abstract String getAlgorithmType();

    public abstract String getAlgorithmImplementation();

    @Nullable
    public abstract List<SegmentBlueprintElement> getSegmentBlueprint();

    @Nullable
    public abstract List<ItemGroup> getPool();

    @Nullable
    public abstract List<SegmentForm> getSegmentForms();

    public static Builder builder() {
        return new AutoValue_Segment.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setPosition(Optional<Integer> newPosition);

        public abstract Builder setAlgorithmType(String newAlgorithmType);

        public abstract Builder setAlgorithmImplementation(String newAlgorithmImplementation);

        @JacksonXmlProperty(localName = "SegmentBlueprint")
        public abstract Builder setSegmentBlueprint(List<SegmentBlueprintElement> newSegmentBlueprint);

        @JacksonXmlProperty(localName = "Pool")
        public abstract Builder setPool(List<ItemGroup> newPool);

        @JacksonXmlProperty(localName = "SegmentForms")
        public abstract Builder setSegmentForms(List<SegmentForm> newSegmentForm);

        public abstract Segment build();
    }
}
