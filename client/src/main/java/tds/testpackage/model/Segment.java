package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.*;

/**
 * Every Assessment must contain one or more segments (an "unsegmented" assessment is actually a single-segment assessment).
 * A segment defines a partition of a test wherein various presentational aspects may be controlled (such as isolating calculator items from non-calculator items)
 *
 * The Segment node contains the following attributes:
 *
 * key: The segment key
 * id: The segment id
 * position: The position of the segment in the assessment (1-based)
 * label: (OPTIONAL) A human-readable label to be displayed in the student and proctor application - if no label is provided, the segment key will be used.
 * entryApproval/exitApproval: (OPTIONAL) A flag indicating that a student will require approval to enter or exit the segment
 *
 * The Segment contains the following sub-elements:
 *
 * - Blueprint: The segment blueprint constraints for selecting items, each references an assessment-level BlueprintElement
 * The Segment Blueprint is optional for fixed form segments, but required for adaptive segments
 * - One of the folowing, depending on the item selection type:
 * Pool - The segment pool of items, for adaptive segments
 * Forms - A list of forms for the segment, for fixed-form assessments
 * - ItemSelector: the method for selecting items for the segment (e.g. "fixedform", "adaptive")
 * - Tools: A list of universal tools, accommodations, and designated supports along with various options and dependency rules
 *
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Segment.Builder.class)
public abstract class Segment {
    public abstract String getId();

    @JsonProperty("position")
    protected abstract Optional<Integer> getPosition();

    protected abstract Optional<String> getEntryApproval();

    protected abstract Optional<String> getExitApproval();

    public boolean entryApproval() {
        return parseBoolean(getEntryApproval(), false);
    }

    public boolean exitApproval() {
        return parseBoolean(getExitApproval(), false);
    }

    public int position() {
        return getPosition().orElse(1);
    }

    public abstract String getAlgorithmType();

    public abstract String getAlgorithmImplementation();

    @Nullable
    protected abstract List<SegmentBlueprintElement> getSegmentBlueprint();

    /**
     * A segment-level Blueprint that defines item selection rules for adaptive segments
     *
     * @return
     */
    @JsonProperty(value = "segmentBlueprint")
    public List<SegmentBlueprintElement> segmentBlueprint() {
        return Optional.ofNullable(getSegmentBlueprint()).orElse(new ArrayList<>());
    }

    @Nullable
    protected abstract List<ItemGroup> getPool();

    /**
     * The Segment's Pool - A list of items/itemgroups that is specific for adaptive segments.
     *
     * A pool containing items that can be selected for a segment, based on adaptive algorithm calculations,
     * min/max item constraits at the strand, contentlevel, segment, or assessment levels.
     *
     * @return
     */
    @JsonProperty(value = "pool")
    public List<ItemGroup> pool() {
        return Optional.ofNullable(getPool()).orElse(new ArrayList<>());
    }

    @Nullable
    protected abstract List<SegmentForm> getSegmentForms();

    /**
     * The forms available form selection for this assessment (typically based on subject and the form cohort),
     * at least one form is required per-segment, for fixed-form segments.
     *
     * @return
     */
    @JsonProperty(value = "segmentForms")
    public List<SegmentForm> segmentForms() {
        return Optional.ofNullable(getSegmentForms()).orElse(new ArrayList<>());
    }

    private Assessment assessment;

    /**
     * Reference to parent assessment to access test package fields to construct composite key
     * @param assessment
     */
    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    /**
     * Reference to parent assessment to access test package fields to construct composite key
     */
    @JsonIgnore
    public Assessment getAssessment() {
        return assessment;
    }


    @JsonIgnore
    public String getKey() {
        return String.format("(%s)%s-%s", assessment.getTestPackage().getPublisher(), getId(), assessment.getTestPackage().getAcademicYear());
    }

    public static Builder builder() {
        return new AutoValue_Segment.Builder().setPosition(1);
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        @JacksonXmlProperty(localName = "position")
        protected abstract Builder setPosition(Optional<Integer> newPosition);

        public Builder setPosition(int newPosition) {
            return setPosition(Optional.of(newPosition));
        }

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
        public abstract Builder setAlgorithmType(String newAlgorithmType);

        public abstract Builder setAlgorithmImplementation(String newAlgorithmImplementation);

        @JacksonXmlProperty(localName = "SegmentBlueprint")
        public abstract Builder setSegmentBlueprint(List<SegmentBlueprintElement> newSegmentBlueprint);

        @JacksonXmlProperty(localName = "Pool")
        public abstract Builder setPool(List<ItemGroup> newPool);

        @JacksonXmlProperty(localName = "SegmentForms")
        public abstract Builder setSegmentForms(List<SegmentForm> newSegmentForm);

        public abstract Segment build();
    }
}
