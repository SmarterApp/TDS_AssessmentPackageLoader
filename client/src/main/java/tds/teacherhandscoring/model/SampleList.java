package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_SampleList.Builder.class)
@JacksonXmlRootElement(localName = "samplelist")
public abstract class SampleList {
    public abstract String getMaxVal();
    public abstract String getMinVal();
    public abstract Optional<String> getScorePoint();
    public abstract List<Sample> getSamples();

    public static Builder builder() {
        return new AutoValue_SampleList.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "maxval")
        public abstract Builder setMaxVal(String newMaxVal);

        @JacksonXmlProperty(localName = "minval")
        public abstract Builder setMinVal(String newMinVal);

        @JacksonXmlProperty(localName = "scorepoint")
        public abstract Builder setScorePoint(Optional<String> newScorePoint);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "sample")
        public abstract Builder setSamples(List<Sample> newSamples);

        public abstract SampleList build();
    }
}
