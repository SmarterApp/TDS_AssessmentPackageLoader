package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_Rubric.Builder.class)
@JacksonXmlRootElement(localName = "rubric")
public abstract class Rubric {
    public abstract String getName();
    public abstract String getVal();
    public abstract String getScorePoint();

    public static Builder builder() {
        return new AutoValue_Rubric.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setVal(String newVal);

        @JacksonXmlProperty(localName = "scorepoint")
        public abstract Builder setScorePoint(String newScorePoint);

        public abstract Rubric build();
    }
}
