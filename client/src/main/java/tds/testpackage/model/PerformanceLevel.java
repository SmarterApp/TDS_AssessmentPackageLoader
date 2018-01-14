package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

/**
 * Cutscore that determines a coarse-grained level of skill-attainment
 * Performance levels are part of scoring and reporting.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_PerformanceLevel.Builder.class)
public abstract class PerformanceLevel {
    @JsonProperty("pLevel")
    public abstract int getPLevel();
    public abstract double getScaledLo();
    public abstract double getScaledHi();

    public static Builder builder() {
        return new AutoValue_PerformanceLevel.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "pLevel")
        @JsonProperty("pLevel")
        public abstract Builder setPLevel(int newPLevel);

        @JacksonXmlProperty(localName = "scaledLo")
        public abstract Builder setScaledLo(double newScaledHi);

        @JacksonXmlProperty(localName = "scaledHi")
        public abstract Builder setScaledHi(double newScaledHi);

        public abstract PerformanceLevel build();
    }
}
