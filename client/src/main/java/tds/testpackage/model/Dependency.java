package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.Optional;

import static tds.testpackage.model.XmlUtil.*;

/**
 * A Dependency defines a rule for when a specific Tool Option should be enabled or available to an examinee.
 *
 *  ATTRIBUTES:
 *  - ifToolType: The conditional tool type of the rule
 *  - ifToolCode: The conditional tool code (option) of the rule
 *  - enabled: (OPTIONAL, default TRUE) - Indicated whether the option should be ENABLED or DISABLED
 *  - default: Whether or not the option should now be marked as the default option, if the Dependency condition is true
 *
 *  The Dependency can be read as:
 *  if <ifToolType> is <ifToolCode>, then the option is enabled (or disabled)
 *  e.g., "IF <Language> IS <ENU>, THEN <'Emboss' option should be disabled>"
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Dependency.Builder.class)
public abstract class Dependency {
    public abstract String getIfToolType();
    public abstract String getIfToolCode();
    @JsonProperty("enabled")
    protected abstract Optional<String> getEnabled();
    protected abstract Optional<String> getDefault();

    public boolean enabled() {
        return parseBoolean(getEnabled(), true);
    }

    public boolean defaultValue() {
        return parseBoolean(getDefault(), false);
    }

    public static Builder builder() {
        return new AutoValue_Dependency.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIfToolType(String newIfToolType);

        public abstract Builder setIfToolCode(String newIfToolCode);

        protected abstract Builder setEnabled(Optional<String> newEnabled);

        public Builder setEnabled(boolean newEnabled) {
            return setEnabled(Optional.of(String.valueOf(newEnabled)));
        }

        @JacksonXmlProperty(localName = "default")
        @JsonProperty("default")
        protected abstract Builder setDefault(Optional<String> newDefault);

        public Builder setDefault(boolean newDefault) {
            return setDefault(Optional.of(String.valueOf(newDefault)));
        }


        public abstract Dependency build();
    }
}
