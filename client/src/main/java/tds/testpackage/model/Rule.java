package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A rule for computing a score element, such as
 * - overall theta or scaled score
 * - strand score
 * - benchmark score
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Rule.Builder.class)
public abstract class Rule {
    @XmlAttribute
    public abstract String getName();
    @XmlAttribute
    public abstract int getComputationOrder();

    @Nullable
    protected abstract List<Parameter> getParameters();

    @XmlElement(name="Parameter", type=Parameter.class)
    @JsonProperty(value = "parameters")
    public List<Parameter> parameters() {
        return Optional.ofNullable(getParameters()).orElse(new ArrayList<>());
    }

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
