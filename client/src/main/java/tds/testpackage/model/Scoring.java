package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Scoring.Builder.class)
public abstract class Scoring {
    @Nullable
    protected abstract List<PerformanceLevel> getPerformanceLevels();

    @JsonProperty("performanceLevels")
    @XmlElementWrapper(name="PerformanceLevels")
    @XmlElement(name="PerformanceLevel", type=PerformanceLevel.class)
    public List<PerformanceLevel> performanceLevels() {
        return Optional.ofNullable(getPerformanceLevels()).orElse(new ArrayList<>());
    }

    /**
     * Configurations for scoring the test, consisting of
     * - rules for computation
     * - score cut points for determining performance levels
     */
    @XmlElementWrapper(name="Rules")
    @XmlElement(name="Rule", type=Rule.class)
    public abstract List<Rule> getRules();

    public static Builder builder() {
        return new AutoValue_Scoring.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "PerformanceLevels")
        public abstract Builder setPerformanceLevels(List<PerformanceLevel> newPerformanceLevels);

        @JacksonXmlProperty(localName = "Rules")
        public abstract Builder setRules(List<Rule> newRules);

        public abstract Scoring build();
    }
}
