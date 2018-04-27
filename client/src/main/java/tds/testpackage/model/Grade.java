package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Optional;

/**
 * The Grade element contains the following attributes:
 value: The grade code/integer value for the grade (e.g., "KR", "1", "12")
 label: The human readable label of the grade (e.g., "Kindergarden", "Grade 12")
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Grade.Builder.class)
public abstract class Grade {
    @XmlAttribute
    public abstract String getValue();

    @XmlAttribute
    public abstract Optional<String> getLabel();

    public static Builder builder() {
        return new AutoValue_Grade.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setValue(String newValue);
        public abstract Builder setLabel(Optional<String> newLabel);

        public abstract Grade build();
    }
}
