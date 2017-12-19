package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Assessment.Builder.class)
public abstract class Assessment {
    public abstract String getId();
    public abstract String getLabel();
    public abstract List<Grade> getGrades();
    public abstract List<Segment> getSegments();
    public abstract List<Tool> getTools();

    public static Builder builder() {
        return new AutoValue_Assessment.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setLabel(String newLabel);

        @JacksonXmlProperty(localName = "Grades")
        public abstract Builder setGrades(List<Grade> newGrades);

        @JacksonXmlProperty(localName = "Segments")
        public abstract Builder setSegments(List<Segment> newSegments);

        @JacksonXmlProperty(localName = "Tools")
        public abstract Builder setTools(List<Tool> newTools);

        public abstract Assessment build();
    }
}
