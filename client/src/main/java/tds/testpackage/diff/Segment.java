package tds.testpackage.diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;
import tds.testpackage.model.Tool;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

@AutoValue
@JsonDeserialize(builder = AutoValue_Segment.Builder.class)
@XmlType(propOrder={"items", "tools"})
public abstract class Segment {
    @XmlAttribute
    public abstract String getId();

    @JsonProperty
    protected abstract Optional<String> getEntryApproval();

    @JsonProperty
    protected abstract Optional<String> getExitApproval();

    @XmlAttribute
    public boolean entryApproval() {
        return parseBoolean(getEntryApproval(), false);
    }

    @XmlAttribute
    public boolean exitApproval() {
        return parseBoolean(getExitApproval(), false);
    }

    @XmlAttribute
    public abstract Optional<String> getLabel();

    @Nullable
    protected abstract List<Item> getItems();

    /**
     * The Segment's Pool - A list of items/itemgroups that is specific for adaptive segments.
     *
     * A pool containing items that can be selected for a segment, based on adaptive algorithm calculations,
     * min/max item constraits at the strand, contentlevel, segment, or assessment levels.
     *
     * @return
     */
    @JsonProperty(value = "items")
    @XmlElementWrapper(name="Items")
    @XmlElement(name="Item", type=Item.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Item> items() {
        return Optional.ofNullable(getItems()).orElse(new ArrayList<>());
    }

    @Nullable
    protected abstract List<Tool> getTools();

    @JsonProperty(value = "tools")
    @XmlElementWrapper(name="Tools")
    @XmlElement(name="Tool", type=Tool.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Tool> tools() {
        return Optional.ofNullable(getTools()).orElse(new ArrayList<>());
    }

    public static Builder builder() {
        return new AutoValue_Segment.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        @JacksonXmlProperty(localName = "entryApproval")
        protected abstract Builder setEntryApproval(Optional<String> newEntryApproval);

        public Builder setEntryApproval(boolean newEntryApproval) {
            return setEntryApproval(Optional.of(String.valueOf(newEntryApproval)));
        }

        @JacksonXmlProperty(localName = "exitApproval")
        protected abstract Builder setExitApproval(Optional<String> newExitApproval);

        public Builder setExitApproval(boolean newExitApproval) {
            return setExitApproval(Optional.of(String.valueOf(newExitApproval)));
        }

        public abstract Builder setLabel(Optional<String> newLabel);

        @JacksonXmlProperty(localName = "Items")
        public abstract Builder setItems(List<Item> newItems);

        @JacksonXmlProperty(localName = "Tools")
        public abstract Builder setTools(List<Tool> newTools);

        public abstract Segment build();
    }
}
