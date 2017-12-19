package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemScoreParameter.Builder.class)
public abstract class ItemScoreParameter {
    public abstract String getMeasurementParameter();
    public abstract double getValue();

    public static Builder builder() {
        return new AutoValue_ItemScoreParameter.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setMeasurementParameter(String newMeasurementParameter);
        public abstract Builder setValue(double newValue);

        public abstract ItemScoreParameter build();
    }
}
