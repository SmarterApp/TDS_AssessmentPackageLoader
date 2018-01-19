package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_ItemGroup.Builder.class)
public abstract class ItemGroup {
    public abstract String getId();

    protected abstract Optional<Integer> getPosition();

    public int position() {
        return getPosition().orElse(1);
    }

    protected abstract Optional<String> getMaxItems();
    public String maxItems() {
        return getMaxItems().orElse("ALL");
    }

    protected abstract Optional<String> getMaxResponses();
    public String maxResponses() {
        return getMaxResponses().orElse("ALL");
    }
    public abstract Optional<Stimulus> getStimulus();

    @Nullable
    public abstract List<Item> getItems();

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
