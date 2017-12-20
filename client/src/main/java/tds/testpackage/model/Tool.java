package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

/**
 * A "Tool" is a general term for any universal tools, designated supports, or accommodations. Each Tool respresents a type of tool, such as "Zoom", "American Sign Language", "Masking", and "Calculator" and contains a list of its possible options and dependency rules. Tools can be defined at the assessment-level as well as the segment-level
 *
 * ATTRIBUTES:
 * name: The tool type "name". This is the string identifying the tool type. The tool type name may contain spaces.
 * studentPackageFieldName: The name/identifier of the tool/accommodation/designated supports used by ART.
 * allowChange: A flag indicating whether or not the tool can be changed by the proctor when a student is waiting for approval to enter an assessment or segment
 * required: A flag indicating whether or not an tool selection is required for this type of tool
 * sortOrder: An optional ordering of the type (ascending)
 * disableOnGuest: A flag indicating whether or not an accommodation should be disabled for guest students
 *
 * SUBELEMENTS:
 * The Options for the Tool
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Tool.Builder.class)
public abstract class Tool {
    public abstract String getName();
    public abstract String getStudentPackageFieldName();
    public abstract Optional<String> getAllowChange();
    public abstract Optional<String> getRequired();
    public abstract Optional<Integer> getSortOrder();
    public abstract Optional<String> getDisableOnGuest();
    public abstract List<Option> getOptions();

    public boolean required() {
        return parseBoolean(getRequired(), false);
    }

    public boolean allowChange() {
        return parseBoolean(getAllowChange(), true);
    }

    public boolean disableOnGuest() {
        return parseBoolean(getDisableOnGuest(), false);
    }

    public static Builder builder() {
        return new AutoValue_Tool.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setStudentPackageFieldName(String newStudentPackageFieldName);

        protected abstract Builder setAllowChange(Optional<String> newAllowChange);

        public Builder setAllowChange(boolean newAllowChange) {
            return setAllowChange(Optional.of(String.valueOf(newAllowChange)));
        }

        protected abstract Builder setRequired(Optional<String> newRequired);

        public Builder setRequired(boolean newRequired) {
            return setRequired(Optional.of(String.valueOf(newRequired)));
        }

        protected abstract Builder setDisableOnGuest(Optional<String> newDisableOnGuest);

        public Builder setDisableOnGuest(boolean newDisableOnGuest) {
            return setDisableOnGuest(Optional.of(String.valueOf(newDisableOnGuest)));
        }

        protected abstract Builder setSortOrder(Optional<Integer> newSortOrder);

        public Builder setSortOrder(int newSortOrder) {
            return setSortOrder(Optional.of(newSortOrder));
        }

        @JacksonXmlProperty(localName = "Options")
        public abstract Builder setOptions(List<Option> newOptions);

        public abstract Tool build();
    }
}
