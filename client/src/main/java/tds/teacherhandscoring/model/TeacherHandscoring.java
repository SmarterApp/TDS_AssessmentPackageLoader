package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = TeacherHandscoring.Builder.class)
abstract public class TeacherHandscoring {
    public abstract String getBaseUrl();

    public abstract String getExamplar();

    public abstract String getTrainingGuide();

    public abstract Rubric getRubric();

    public abstract String getDimensions();

    public static Builder builder() {
        return new Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setBaseUrl(String newBaseUrl);

        public abstract Builder setExamplar(String newExamplar);

        public abstract Builder setTrainingGuide(String newTrainingGuide);

        public abstract Builder setRubric(Rubric newRubric);

        public abstract Builder setDimensions(String newDimensions);

        public abstract TeacherHandscoring build();
    }
}
