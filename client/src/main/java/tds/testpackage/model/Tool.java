package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Tool.Builder.class)
public abstract class Tool {
    public abstract String getName();
    public abstract String getStudentPackageFieldName();
    public abstract Optional<String> getAllowChange();
    public abstract List<Option> getOptions();

    public static Builder builder() {
        return new AutoValue_Tool.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setStudentPackageFieldName(String newStudentPackageFieldName);

        public abstract Builder setAllowChange(Optional<String> newAllowChange);

        @JacksonXmlProperty(localName = "Options")
        public abstract Builder setOptions(List<Option> newOptions);

        public abstract Tool build();
    }
}
