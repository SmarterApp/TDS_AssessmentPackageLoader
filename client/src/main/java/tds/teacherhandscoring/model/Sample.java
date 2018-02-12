package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = Sample.Builder.class)
public abstract class Sample {
    public abstract String getName();
    public abstract String getAnnotation();
    public abstract String getSamplecontent();
    public abstract String getPurpose();
    public abstract byte getScorepoint();

    public static Builder builder() {
        return new Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setAnnotation(String newAnnotation);

        public abstract Builder setSamplecontent(String newSamplecontent);

        public abstract Builder setPurpose(String newPurpose);

        public abstract Builder setScorepoint(byte newScorepoint);

        public abstract Sample build();
    }

    public static Builder builder() {
        return new Builder();
    }
}
