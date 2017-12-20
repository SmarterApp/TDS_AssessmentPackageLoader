package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

/**
 * A list of two or more Options for the given Tool.
 * Typically, a tool will have at the very least an "ON" or "OFF" option
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Option.Builder.class)
public abstract class Option {
    public abstract String getCode();
    public abstract int getSortOrder();
    protected abstract Optional<String> getDefault();
    public boolean defaultValue() {
        return parseBoolean(getDefault(), false);
    }

    /**
     * (OPTIONAL) A list of one or more dependencies indicating whether an
     * Option(s) should be enabled based on other Tool selections
     *
     * @return
     */
    @Nullable
    public abstract List<Dependency> getDependencies();

    public static Builder builder() {
        return new AutoValue_Option.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setCode(String newCode);

        public abstract Builder setSortOrder(int newSortOrder);

        @JacksonXmlProperty(localName = "default")
        protected abstract Builder setDefault(Optional<String> newDefault);


        public Builder setDefault(boolean newDefault) {
            return setDefault(Optional.of(String.valueOf(newDefault)));
        }

        @JacksonXmlProperty(localName = "Dependencies")
        public abstract Builder setDependencies(List<Dependency> newDependencies);

        public abstract Option build();
    }
}
