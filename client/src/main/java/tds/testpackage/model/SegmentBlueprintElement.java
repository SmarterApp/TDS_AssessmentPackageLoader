package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_SegmentBlueprintElement.Builder.class)
public abstract class SegmentBlueprintElement {
    public abstract String getIdRef();
    public abstract int getMinExamItems();
    public abstract int getMaxExamItems();
    public abstract List<Property> getItemSelection();

    public static Builder builder() {
        return new AutoValue_SegmentBlueprintElement.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIdRef(String newIdRef);

        public abstract Builder setMinExamItems(int newMinExamItems);

        public abstract Builder setMaxExamItems(int newMaxExamItems);

        @JacksonXmlProperty(localName = "ItemSelection")
        public abstract Builder setItemSelection(List<Property> newItemSelection);

        public abstract SegmentBlueprintElement build();
    }
}
