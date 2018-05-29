package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.springframework.data.annotation.Transient;
import tds.teacherhandscoring.model.TeacherHandScoring;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

@AutoValue
@JsonDeserialize(builder = AutoValue_Item.Builder.class)
public abstract class Item {
    @XmlAttribute
    public abstract String getId();
    @XmlAttribute
    public abstract String getType();

    @XmlElementWrapper(name="Presentations")
    @XmlElement(name="Presentation", type=Presentation.class)
    public abstract List<Presentation> getPresentations();

    @XmlElementWrapper(name="BlueprintReferences")
    @XmlElement(name="BlueprintReference", type=BlueprintReference.class)
    public abstract List<BlueprintReference> getBlueprintReferences();

    @XmlElementWrapper(name="ItemScoreDimensions")
    @XmlElement(name="ItemScoreDimension", type=ItemScoreDimension.class)
    public abstract List<ItemScoreDimension> getItemScoreDimensions();

    protected abstract Optional<String> getFieldTest();
    @XmlAttribute
    public boolean fieldTest() {
        return parseBoolean(getFieldTest(), false);
    }

    protected abstract Optional<String> getAdministrationRequired();
    @XmlAttribute
    public boolean administrationRequired() {
        return parseBoolean(getAdministrationRequired(), true);
    }

    protected abstract Optional<String> getActive();
    @XmlAttribute
    public boolean active() {
        return parseBoolean(getActive(), true);
    }

    protected abstract Optional<String> getResponseRequired();
    @XmlAttribute
    public boolean responseRequired() {
        return parseBoolean(getResponseRequired(), true);
    }

    protected abstract Optional<String> getHandScored();
    @XmlAttribute
    public boolean handScored() {
        return parseBoolean(getHandScored(), false);
    }

    protected abstract Optional<String> getDoNotScore();
    @XmlAttribute
    public boolean doNotScore() {
        return parseBoolean(getDoNotScore(), false);
    }

    @XmlElement(name = "TeacherHandScoring")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract Optional<TeacherHandScoring> getTeacherHandScoring();

    @Transient
    @XmlTransient
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

    @Transient
    @XmlTransient
    private ItemGroup itemGroup;

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    @JsonIgnore
    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    @Transient
    @XmlTransient
    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    @Transient
    @XmlTransient
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @JsonIgnore
    @XmlTransient
    public String getKey() {
        return String.format("%s-%s", getTestPackage().getBankKey(), getId());
    }

    /**
     * Item's position in its parents segment form.
     * If there is no segment form, returns position in the item group.
     *
     * @return item's position
     */
    @JsonIgnore
    @Transient
    @XmlTransient
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

    abstract Builder toBuilder();

    public Item withPresentations(List<Presentation> presentations) {
        return toBuilder().setPresentations(presentations).build();
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

        @JacksonXmlProperty(localName = "ItemScoreDimensions")
        public abstract Builder setItemScoreDimensions(List<ItemScoreDimension> newItemScoreDimensions);

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
