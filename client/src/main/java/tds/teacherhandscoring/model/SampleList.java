package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = SampleList.Builder.class)
public abstract class SampleList {
    public abstract String getMaxVal();
    public abstract String getMinVal();
    public abstract String getScorepoint();
    public abstract List<Sample> getSamples();

    public static Builder builder() {
        return new AutoValue_SampleList.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setMaxVal(String newMaxVal);

        public abstract Builder setMinVal(String newMinVal);

        public abstract Builder setScorepoint(String newScorepoint);

        public abstract Builder setSamples(List<Sample> newSamples);

        public abstract SampleList build();
    }
}
