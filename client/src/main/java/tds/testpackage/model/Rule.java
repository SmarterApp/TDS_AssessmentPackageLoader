package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A rule for computing a score element, such as
 * - overall theta or scaled score
 * - strand score
 * - benchmark score
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Rule.Builder.class)
public abstract class Rule {
    public abstract String getName();
    public abstract int getComputationOrder();
    @Nullable
    public abstract List<Parameter> getParameters();

    public static Builder builder() {
        return new AutoValue_Rule.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setComputationOrder(int newComputationOrder);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Parameter")
        public abstract Builder setParameters(List<Parameter> newParameters);

        public abstract Rule build();
    }
}
