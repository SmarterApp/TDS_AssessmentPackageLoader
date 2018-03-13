package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonDeserialize(builder = AutoValue_SegmentForm.Builder.class)
public abstract class SegmentForm {
    @XmlAttribute
    public abstract String getId();
    @XmlAttribute
    public abstract String getCohort();

    @XmlElementWrapper(name="Presentations")
    @XmlElement(name="Presentation", type=Presentation.class)
    public abstract List<Presentation> getPresentations();

    @Nullable
    protected abstract List<ItemGroup> getItemGroups();

    @JsonProperty(value = "itemGroups")
    @XmlElement(name="ItemGroup", type=ItemGroup.class)
    public List<ItemGroup> itemGroups() {
        return Optional.ofNullable(getItemGroups()).orElse(new ArrayList<>());
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
        return new AutoValue_SegmentForm.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setCohort(String newCohort);

        @JacksonXmlProperty(localName = "Presentations")
        public abstract Builder setPresentations(List<Presentation> newPresentations);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ItemGroup")
        public abstract Builder setItemGroups(List<ItemGroup> newItemGroups);

        public abstract SegmentForm build();
    }
}
