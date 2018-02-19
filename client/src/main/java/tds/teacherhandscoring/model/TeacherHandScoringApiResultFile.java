package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_TeacherHandScoringApiResultFile.Builder.class)
public abstract class TeacherHandScoringApiResultFile {
    public abstract String getFileName();
    public abstract boolean getSuccess();
    @Nullable
    public abstract String getErrorMessage();

    public static Builder builder() {
        return new AutoValue_TeacherHandScoringApiResultFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(value = "FileName")
        public abstract Builder setFileName(String newFileName);

        @JsonProperty(value = "Success")
        public abstract Builder setSuccess(boolean newSuccess);

        @JsonProperty(value = "ErrorMessage")
        public abstract Builder setErrorMessage(String newErrorMessage);

        public abstract TeacherHandScoringApiResultFile build();
    }
}
