package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Sample.Builder.class)
public abstract class Sample {
    public abstract String getName();
    public abstract Optional<String> getAnnotation();
    public abstract String getSampleContent();
    public abstract String getPurpose();
    public abstract byte getScorePoint();

    public static Builder builder() {
        return new AutoValue_Sample.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setAnnotation(Optional<String> newAnnotation);

        @JacksonXmlProperty(localName = "samplecontent")
        public abstract Builder setSampleContent(String newSampleContent);

        public abstract Builder setPurpose(String newPurpose);

        @JacksonXmlProperty(localName = "scorepoint")
        public abstract Builder setScorePoint(byte newScorePoint);

        public abstract Sample build();
    }
}
