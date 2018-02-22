package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Presentation.Builder.class)
/**
 * Presentation/language (code) compatible with the given item or form.
 * For fixed form item selection, an item MUST contain the language of the form it belongs to in order to be compatible.
 */
public abstract class Presentation {
    protected static Map<String, String> DEFAULT_LABELS = ImmutableMap.of(
        "ENU", "English",
        "ESN", "Spanish",
        "ENU-Braille", "Braille");

    protected abstract Optional<String> getLabel();

    /**
     * Label provided in test specification,
     * or label provided from list of known language codes,
     * or the language code if label could not be found.
     */
    public String label() {
        return getLabel().orElse(DEFAULT_LABELS.getOrDefault(getCode(), getCode()));
    }

    public abstract String getCode();

    public static Builder builder() {
        return new AutoValue_Presentation.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "label")
        public abstract Builder setLabel(Optional<String> newLabel);

        public Builder setLabel(String newLabel) {
            setLabel(Optional.of(newLabel));
            return this;
        }

        public abstract Builder setCode(String newCode);

        abstract String getCode(); // must match method name in Presentation

        abstract Presentation autoBuild(); // not public

        public Presentation build() {
            setCode(getCode().trim());
            return autoBuild();
        }
    }
}
