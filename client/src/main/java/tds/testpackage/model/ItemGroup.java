package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemGroup.Builder.class)
public abstract class ItemGroup {
    @XmlAttribute
    public abstract String getId();

    protected abstract Optional<Integer> getPosition();

    @XmlTransient
    public int position() {
        return getPosition().orElse(1);
    }

    protected abstract Optional<String> getMaxItems();
    @XmlAttribute
    public String maxItems() {
        return getMaxItems().orElse("ALL");
    }

    protected abstract Optional<String> getMaxResponses();
    @XmlAttribute
    public String maxResponses() {
        return getMaxResponses().orElse("ALL");
    }

    @XmlElement(name = "Stimulus")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract Optional<Stimulus> getStimulus();

    @JsonIgnore
    @XmlTransient
    public String getKey() {
        /*
            I-<bankkey>-<groupid> OR
            G-<bankkey>-groupid>-<segmentPosition>

            Rules:
                If the item group contains more than one item, the prefix is "G", and the suffix is the
                segment position (or "0" if this is a non-segmented assessment)

                If the item group contains only one item, the prefix is "I" and there is no suffix
         */
        final String prefix = items().size() > 1 ? "G" : "I";
        final String groupKey = items().size() > 1
            ? String.format("%s-%s-%s-%s", prefix, getSegment().getAssessment().getTestPackage().getBankKey(), getId(), getGroupSuffix())
            : String.format("%s-%s-%s", prefix, getSegment().getAssessment().getTestPackage().getBankKey(), getId());

        return groupKey;
    }

    private String getGroupSuffix() {
        if (getSegment().getAssessment().isSegmented()) {
            return String.valueOf(getSegment().position());
        }

        return "0";
    }

    @Nullable
    protected abstract List<Item> getItems();
    @JsonProperty(value = "items")
    @XmlElement(name="Item", type=Item.class)
    public List<Item> items() {
        return Optional.ofNullable(getItems()).orElse(new ArrayList<>());
    }

    @Transient
    @XmlTransient
    private Segment segment;
    public void setSegment(Segment segment) {
        this.segment = segment;
    }
    @JsonIgnore
    public Segment getSegment() {
        return this.segment;
    }

    public static Builder builder() {
        return new AutoValue_ItemGroup.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        @JacksonXmlProperty(localName = "position")
        public abstract Builder setPosition(Optional<Integer> newPosition);

        @JacksonXmlProperty(localName = "maxItems")
        public abstract Builder setMaxItems(Optional<String> newMaxItems);

        @JacksonXmlProperty(localName = "maxResponses")
        public abstract Builder setMaxResponses(Optional<String> newMaxResponses);

        @JacksonXmlProperty(localName = "Stimulus")
        public abstract Builder setStimulus(Optional<Stimulus> newStimulus);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Item")
        public abstract Builder setItems(List<Item> newItems);

        public abstract ItemGroup build();
    }
}
