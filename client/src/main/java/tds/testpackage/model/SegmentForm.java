package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_SegmentForm.Builder.class)
public abstract class SegmentForm {
    public abstract String getId();
    public abstract String getCohort();
    public abstract List<String> getPresentations();
    public abstract ItemGroup getItemGroup();

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

        @JacksonXmlProperty(localName = "ItemGroup")
        public abstract Builder setItemGroup(ItemGroup newItemGroup);

        public abstract SegmentForm build();
    }
}
