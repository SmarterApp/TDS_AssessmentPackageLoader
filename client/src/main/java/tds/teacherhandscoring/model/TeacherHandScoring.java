package tds.teacherhandscoring.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = TeacherHandScoring.Builder.class)
abstract public class TeacherHandScoring {
    public abstract String getBaseUrl();

    public abstract String getExamplar();

    public abstract String getTrainingGuide();

    public abstract List<Object> getRubricList();

    public abstract String getDimensions();

    public static Builder builder() {
        return new AutoValue_TeacherHandScoring.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setBaseUrl(String newBaseUrl);

        public abstract Builder setExamplar(String newExamplar);

        public abstract Builder setTrainingGuide(String newTrainingGuide);

        @JacksonXmlProperty(localName = "rubriclist")
        public abstract Builder setRubricList(List<Object> newRubricList);

        public abstract Builder setDimensions(String newDimensions);

        public abstract TeacherHandScoring build();
    }
}
