package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

//@JsonDeserialize(builder = AutoValue_Item.Builder.class)

// (SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016
@AutoValue
@JsonDeserialize(using = ItemXmlDeserializer.class, builder = AutoValue_Item.Builder.class)
public abstract class Item {
    protected abstract String getClientId();
    protected abstract String getAcademicYear();
    public String getKey() {
        return String.format("(%s)%s-%s", getClientId(), getId(), getAcademicYear());
    }
    public abstract String getId();
    public abstract String getType();
    public abstract Optional<Integer> getPosition();
    public abstract List<String> getPresentations();
    public abstract List<BlueprintReference> getBlueprintReferences();
    public abstract ItemScoreDimension getItemScoreDimension();

    public static Builder builder() {
        return new AutoValue_Item.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        @JsonIgnore
        public abstract Builder setClientId(String newClientId);

        @JsonIgnore
        public abstract Builder setAcademicYear(String newAcademicYear);

        public abstract Builder setId(String newId);

        public abstract Builder setType(String newType);

        @JacksonXmlProperty(localName = "position")
        public abstract Builder setPosition(Optional<Integer> newPosition);

        @JacksonXmlProperty(localName = "Presentations")
        public abstract Builder setPresentations(List<String> newPresentations);

        @JacksonXmlProperty(localName = "BlueprintReferences")
        public abstract Builder setBlueprintReferences(List<BlueprintReference> newBlueprintReferences);

        @JacksonXmlProperty(localName = "ItemScoreDimension")
        public abstract Builder setItemScoreDimension(ItemScoreDimension newItemScoreDimension);

        public abstract Item build();
    }
}
