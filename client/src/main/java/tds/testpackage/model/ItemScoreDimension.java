package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemScoreDimension.Builder.class)
public abstract class ItemScoreDimension {
    public abstract String getMeasurementModel();
    public abstract int getScorePoints();
    public abstract double getWeight();
    public abstract Optional<String> getDimension();
    @Nullable
    public abstract List<ItemScoreParameter> getItemScoreParameters();

    public static Builder builder() {
        return new AutoValue_ItemScoreDimension.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setMeasurementModel(String newMeasurementModel);

        public abstract Builder setScorePoints(int newScorePoints);

        public abstract Builder setWeight(double newWeight);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ItemScoreParameter")
        public abstract Builder setItemScoreParameters(List<ItemScoreParameter> newItemScoreParameters);

        public abstract Builder setDimension(Optional<String> newDimension);

        public abstract ItemScoreDimension build();
    }
}
