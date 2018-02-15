package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(using = RubricListDeserializer.class, builder = AutoValue_RubricList.Builder.class)
public abstract class RubricList {

    public abstract List<Rubric> getRubrics();
    public abstract List<SampleList> getSampleLists();

    public static Builder builder() {
        return new AutoValue_RubricList.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {

        public abstract Builder setSampleLists(List<SampleList> sampleList);

        public abstract Builder setRubrics(List<Rubric> rubrics);

        public abstract RubricList build();
    }
}
