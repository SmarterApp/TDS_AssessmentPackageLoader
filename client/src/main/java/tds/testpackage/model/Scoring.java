package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Scoring.Builder.class)
public abstract class Scoring {
    @Nullable
    public abstract List<PerformanceLevel> getPerformanceLevels();
    public abstract List<Rule> getRules();

    public static Builder builder() {
        return new AutoValue_Scoring.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "PerformanceLevels")
        public abstract Builder setPerformanceLevels(List<PerformanceLevel> newPerformanceLevels);

        @JacksonXmlProperty(localName = "Rules")
        public abstract Builder setRules(List<Rule> newRules);

        public abstract Scoring build();
    }
}
