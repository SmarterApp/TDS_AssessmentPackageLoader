package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_TeacherHandScoringDeleteApiResult.Builder.class)
public abstract class TeacherHandScoringDeleteApiResult {
    public abstract String getBankKey();
    @Nullable
    public abstract String getItemKeys();

    public abstract boolean getSuccess();
    @Nullable
    public abstract String getErrorMessage();

    public boolean hasError() {
        return !getSuccess();
    }

    public static Builder builder() {
        return new AutoValue_TeacherHandScoringDeleteApiResult.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(value = "BankKey")
        public abstract Builder setBankKey(String newBankKey);

        @JsonProperty(value = "ItemKeys")
        public abstract Builder setItemKeys(String newItemKeys);

        @JsonProperty(value = "Success")
        public abstract Builder setSuccess(boolean newSuccess);

        @JsonProperty(value = "ErrorMessage")
        public abstract Builder setErrorMessage(String newErrorMessage);

        public abstract TeacherHandScoringDeleteApiResult build();
    }
}
