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

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_SegmentForm.Builder.class)
@XmlType(propOrder={"presentations", "itemGroups"})
public abstract class SegmentForm {
    private static String getFormIdForLanguage(final String formId, final String languageCode) {
        switch (languageCode) {
            case "ESN":
                return String.format("%s::%s", formId, "SPA");
            case "ENU-Braille":
                return String.format("%s::%s", formId, "BRL");
            default:
                return String.format("%s::%s", formId, languageCode);
        }
    }

    @XmlAttribute
    public abstract String getId();

    @JsonIgnore
    public String id(String language) {
        return getFormIdForLanguage(getId(), language);
    }

    @JsonIgnore
    public int key(String language) {
        return id(language).hashCode();
    }

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

    @JsonIgnore
    public abstract Builder toBuilder();

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
