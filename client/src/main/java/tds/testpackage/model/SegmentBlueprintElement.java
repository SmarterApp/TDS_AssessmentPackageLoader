package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BlueprintElement

 ATTRIBUTES:
 - refId: The id of the assessment-level blueprint element reference
 - minExamItems/maxExamItems: the minimum and maximum operational items to administer from this segment
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_SegmentBlueprintElement.Builder.class)
public abstract class SegmentBlueprintElement {
    /**
     * The id of the assessment-level blueprint element reference
     *
     * @return
     */
    public abstract String getIdRef();

    /**
     * Minimum operational items to administer from this segment
     *
     * @return
     */
    public abstract int getMinExamItems();

    /**
     * Maximum operational items to administer from this segment
     *
     * @return
     */
    public abstract int getMaxExamItems();

    protected abstract Optional<Integer> getMinFieldTestItems();

    public int minFieldTestItems() {
        return getMinFieldTestItems().orElse(0);
    }

    protected abstract Optional<Integer> getMaxFieldTestItems();

    public int maxFieldTestItems() {
        return getMaxFieldTestItems().orElse(0);
    }

    @Nullable
    protected abstract List<Property> getItemSelection();

    @JsonProperty(value = "itemSelection")
    public List<Property> itemSelection() {
        return Optional.ofNullable(getItemSelection()).orElse(new ArrayList<>());
    }

    public static Builder builder() {
        return new AutoValue_SegmentBlueprintElement.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setIdRef(String newIdRef);

        public abstract Builder setMinExamItems(int newMinExamItems);

        public abstract Builder setMaxExamItems(int newMaxExamItems);

        protected abstract Builder setMinFieldTestItems(Optional<Integer> newMinFieldTestItems);

        public Builder setMinFieldTestItems(int newMinFieldTestItems) {
            return setMinFieldTestItems(newMinFieldTestItems);
        }

        protected abstract Builder setMaxFieldTestItems(Optional<Integer> newMaxFieldTestItems);

        public Builder setMaxFieldTestItems(int newMaxFieldTestItems) {
            return setMaxFieldTestItems(newMaxFieldTestItems);
        }

        @JacksonXmlProperty(localName = "ItemSelection")
        protected abstract Builder setItemSelection(List<Property> newItemSelection);

        public abstract SegmentBlueprintElement build();
    }
}
