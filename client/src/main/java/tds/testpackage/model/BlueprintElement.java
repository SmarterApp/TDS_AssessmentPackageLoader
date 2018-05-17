package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Each element or component of a blueprint must have a corresponding BlueprintElement.
 A BlueprintElement identifies a set of items for one or both of the following purposes:
 Creating a test 'form' adaptively,
 Scoring a test.

 In addition to a subject's taxonomy of content standards, the following are also BlueprintElements:
 The test itself (for establishing scoring and reporting features)
 Every test segment (for item selection and administration)
 Depth of Knowledge categories
 Other (arbitrary) sets of items (may be useful for ad hoc identification of sets)

 Depth of Knowledge and other arbitrary sets of items are lumped into one object type called an 'AffinityGroup'.
 This is also used for the SBAC Content Standards Repository object called 'SOCK', an acronym for 'Some Other Category of Knowledge'

 ATTRIBUTES:
 - id: The id of the blueprint element
 - type: an arbitrary designation, usually from some aspect of a taxonomy defining the subject-area. (e.g., "strand", "contentlevel")
 Other elementtypes exist outside of the taxonomy
 - parentId: the id of another bpelement, segment, or assessment, generally within the taxonomy.
 Since a BlueprintElement may have at most one parent, at most hierarchies may be expressed.
 - minExamItems/maxExamItems: the minimum and maximum operational items to administer from this blueprint category
 - minFieldTestItems/maxFieldTestItems: (OPTIONAL) the minimum and maximum field test items to administer - default to "0"

 */
@AutoValue
@JsonDeserialize(builder = AutoValue_BlueprintElement.Builder.class)
@XmlType(propOrder={"scoring", "blueprintElements"})
public abstract class BlueprintElement {
    @XmlAttribute
    public abstract String getId();
    @XmlAttribute
    public abstract String getType();

    @XmlAttribute
    public abstract Optional<String> getLevel();

    @XmlAttribute
    public abstract Optional<String> getLabel();

    @XmlAttribute
    public abstract Optional<String> getDescription();

    @Transient
    @XmlTransient
    private Optional<BlueprintElement> parentBlueprintElement;

    public void setParentBlueprintElement(final BlueprintElement parentBlueprintElement) {
        this.parentBlueprintElement = parentBlueprintElement != null
                ? Optional.of(parentBlueprintElement)
                : Optional.empty();
    }

    @JsonIgnore
    public Optional<BlueprintElement> getParentBlueprintElement() {
        return this.parentBlueprintElement;
    }

    @XmlElement(name = "Scoring")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract Optional<Scoring> getScoring();

    @Nullable
    protected abstract List<BlueprintElement> getBlueprintElements();


    @JsonProperty(value = "blueprintElements")
    @XmlElement(name = "BlueprintElement")
    public List<BlueprintElement> blueprintElements() {
        return Optional.ofNullable(getBlueprintElements()).orElse(new ArrayList<>());
    }

    public static Builder builder() {
        return new AutoValue_BlueprintElement.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setType(String newType);

        public abstract Builder setLabel(Optional<String> newLabel);

        public abstract Builder setLevel(Optional<String> newLevel);

        public abstract Builder setDescription(Optional<String> newDescription);

        @JacksonXmlProperty(localName = "Scoring")
        public abstract Builder setScoring(Optional<Scoring> newScoring);

        public Builder setScoring(Scoring newScoring) {
            setScoring(Optional.of(newScoring));
            return this;
        }

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "BlueprintElement")
        public abstract Builder setBlueprintElements(List<BlueprintElement> newBlueprintElements);

        public abstract BlueprintElement build();
    }
}
