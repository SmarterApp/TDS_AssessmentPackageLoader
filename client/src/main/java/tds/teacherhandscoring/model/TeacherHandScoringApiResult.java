package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_TeacherHandScoringApiResult.Builder.class)
public abstract class TeacherHandScoringApiResult {
    public abstract List<TeacherHandScoringApiResultFile> getFiles();

    public static Builder builder() {
        return new AutoValue_TeacherHandScoringApiResult.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(value = "Files")
        public abstract Builder setFiles(List<TeacherHandScoringApiResultFile> newFiles);

        public abstract TeacherHandScoringApiResult build();
    }
}
