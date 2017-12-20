package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Item.Builder.class)
public abstract class Item {
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
