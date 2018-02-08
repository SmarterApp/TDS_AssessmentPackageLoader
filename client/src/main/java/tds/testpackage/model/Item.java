package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tds.testpackage.model.XmlUtil.parseBoolean;

@AutoValue
@JsonDeserialize(builder = AutoValue_Item.Builder.class)
public abstract class Item {
    public abstract String getId();
    public abstract String getType();

    @JsonProperty
    public int position() {
        return 1;
    }

    public abstract List<Presentation> getPresentations();
    public abstract List<BlueprintReference> getBlueprintReferences();
    public abstract ItemScoreDimension getItemScoreDimension();

    protected abstract Optional<String> getFieldTest();
    public boolean fieldTest() {
        return parseBoolean(getFieldTest(), false);
    }

    protected abstract Optional<String> getAdministrationRequired();
    public boolean administrationRequired() {
        return parseBoolean(getAdministrationRequired(), true);
    }

    protected abstract Optional<String> getActive();
    public boolean active() {
        return parseBoolean(getActive(), true);
    }

    protected abstract Optional<String> getResponseRequired();
    public boolean responseRequired() {
        return parseBoolean(getResponseRequired(), true);
    }

    protected abstract Optional<String> getHandScored();
    public boolean handScored() {
        return parseBoolean(getHandScored(), false);
    }

    protected abstract Optional<String> getDoNotScore();
    public boolean doNotScore() {
        return parseBoolean(getDoNotScore(), false);
    }

    private ItemGroup itemGroup;

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    @JsonIgnore
    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    @JsonIgnore
    public String getKey() {
        return String.format("%s-%s", getItemGroup().getSegment().getAssessment().getTestPackage().getBankKey(), getId());
    }

    public static Builder builder() {
        return new AutoValue_Item.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setType(String newType);

        @JacksonXmlProperty(localName = "Presentations")
        public abstract Builder setPresentations(List<Presentation> newPresentations);

        @JacksonXmlProperty(localName = "BlueprintReferences")
        public abstract Builder setBlueprintReferences(List<BlueprintReference> newBlueprintReferences);

        @JacksonXmlProperty(localName = "ItemScoreDimension")
        public abstract Builder setItemScoreDimension(ItemScoreDimension newItemScoreDimension);

        @JacksonXmlProperty(localName = "fieldTest")
        public abstract Builder setFieldTest(Optional<String> newFieldTest);

        @JacksonXmlProperty(localName = "administrationRequired")
        public abstract Builder setAdministrationRequired(Optional<String> newAdministrationRequired);

        @JacksonXmlProperty(localName = "active")
        public abstract Builder setActive(Optional<String> newActive);

        @JacksonXmlProperty(localName = "responseRequired")
        public abstract Builder setResponseRequired(Optional<String> newResponseRequired);

        @JacksonXmlProperty(localName = "handScored")
        public abstract Builder setHandScored(Optional<String> newHandScored);

        @JacksonXmlProperty(localName = "doNotScore")
        public abstract Builder setDoNotScore(Optional<String> newDoNotScore);

        public abstract Item build();
    }
}
