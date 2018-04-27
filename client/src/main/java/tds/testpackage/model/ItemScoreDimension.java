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

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemScoreDimension.Builder.class)
public abstract class ItemScoreDimension {
    @XmlAttribute
    public abstract String getMeasurementModel();
    @XmlAttribute
    public abstract int getScorePoints();
    @XmlAttribute
    public abstract double getWeight();
    @XmlAttribute
    public abstract Optional<String> getDimension();

    @Nullable
    protected abstract List<ItemScoreParameter> getItemScoreParameters();

    @JsonProperty(value="itemScoreParameters")
    @XmlElement(name="ItemScoreParameter", type=ItemScoreParameter.class)
    public List<ItemScoreParameter> itemScoreParameters() {
        return Optional.ofNullable(getItemScoreParameters()).orElse(new ArrayList<>());
    }

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
