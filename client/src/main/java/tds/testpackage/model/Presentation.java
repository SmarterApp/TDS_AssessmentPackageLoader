package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Presentation.Builder.class)
/**
 * Presentation/language (code) compatible with the given item or form.
 * For fixed form item selection, an item MUST contain the language of the form it belongs to in order to be compatible.
 */
public abstract class Presentation {
    protected abstract Optional<String> getLabel();

    public String label() {
        return getLabel().orElse("Default Label");
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
