package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_SegmentForm.Builder.class)
public abstract class SegmentForm {
    public abstract String getId();
    public abstract String getCohort();
    public abstract List<String> getPresentations();
    @Nullable
    protected abstract List<ItemGroup> getItemGroups();
    @JsonProperty(value = "itemGroups")
    public List<ItemGroup> itemGroups() {
        return Optional.ofNullable(getItemGroups()).orElse(new ArrayList<>());
    }

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
        public abstract Builder setPresentations(List<String> newPresentations);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ItemGroup")
        public abstract Builder setItemGroups(List<ItemGroup> newItemGroups);

        public abstract SegmentForm build();
    }
}
