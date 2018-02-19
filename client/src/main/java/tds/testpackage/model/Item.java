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

import tds.teacherhandscoring.model.TeacherHandScoring;

import static tds.testpackage.model.XmlUtil.parseBoolean;

@AutoValue
@JsonDeserialize(builder = AutoValue_Item.Builder.class)
public abstract class Item {
    public abstract String getId();
    public abstract String getType();

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

    public abstract Optional<TeacherHandScoring> getTeacherHandScoring();

    private SegmentForm segmentForm;
    public void setSegmentForm(SegmentForm segmentForm) {
        this.segmentForm = segmentForm;
    }

    /**
     * An item may belong to a segment form
     *
     * @returns parent segment form of this item
     */
    @JsonIgnore
    public Optional<SegmentForm> getSegmentForm() {
        return Optional.ofNullable(this.segmentForm);
    }

    private ItemGroup itemGroup;

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    @JsonIgnore
    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @JsonIgnore
    public String getKey() {
        return String.format("%s-%s", getTestPackage().getBankKey(), getId());
    }

    /**
     * Item's position in its parents segment form.
     * If there is no segment form, returns position in the item group.
     *
     * @return item's position
     */
    @JsonProperty
    public int position() {
        if (getSegmentForm().isPresent()) {
            // jdk9 provides a take until method that would simplify this logic
            // segmentForm.itemGroups().takeUntil(itemGroup.items().indexOf(this) > -1).
            // map(itemGroup.items().size).sum + getItemGroup().items().indexOf(this) + 1
            int position = 0;
            final SegmentForm segmentForm = getSegmentForm().get();
            for(final ItemGroup itemGroup: segmentForm.itemGroups()) {
                final int index = itemGroup.items().indexOf(this);
                if (index != -1) {
                    position += index + 1;
                    break;
                } else {
                    position += itemGroup.items().size();
                }
            }
            return position;
        } else {
            return getItemGroup().items().indexOf(this) + 1;
        }
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

        @JacksonXmlProperty(localName = "TeacherHandScoring")
        public abstract Builder setTeacherHandScoring(Optional<TeacherHandScoring> newTeacherHandScoring);

        @JsonProperty(value = "position")
        /**
         *  Ignore setting position value.
         *  Position is derived from it's nature order in the item group or segment form.
         */
        public Builder setPosition(int newPosition) {
            return this;
        }

        public abstract Item build();
    }
}
