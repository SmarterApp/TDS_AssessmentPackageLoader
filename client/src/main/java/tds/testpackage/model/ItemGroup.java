package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemGroup.Builder.class)
public abstract class ItemGroup {
    public abstract String getId();

    public abstract Optional<Stimulus> getStimulus();

    public abstract List<Item> getItems();

    public static Builder builder() {
        return new AutoValue_ItemGroup.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        @JacksonXmlProperty(localName = "Stimulus")
        public abstract Builder setStimulus(Optional<Stimulus> newStimulus);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Item")
        public abstract Builder setItems(List<Item> newItems);

        public abstract ItemGroup build();
    }
}
