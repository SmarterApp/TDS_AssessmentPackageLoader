package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemScoreDimension.Builder.class)
public abstract class ItemScoreDimension {
    public abstract String getMeasurementModel();
    public abstract String getScorePoints();
    public abstract double getWeight();
    public abstract List<ItemScoreParameter> getItemScoreParameters();

    public static Builder builder() {
        return new AutoValue_ItemScoreDimension.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setMeasurementModel(String newMeasurementModel);

        public abstract Builder setScorePoints(String newScorePoints);

        public abstract Builder setWeight(double newWeight);

        @JacksonXmlProperty(localName = "ItemScoreParameter")
        public abstract Builder setItemScoreParameters(List<ItemScoreParameter> newItemScoreParameters);

        public abstract ItemScoreDimension build();
    }
}
