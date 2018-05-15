package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.springframework.data.annotation.Id;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The test package.
 * contains an optional <Scoring> element (for test packages that contain COMBINED scoring data, such as ICA assessments).
 */
@AutoValue
@JsonDeserialize(using = TestPackageDeserializer.class,  builder = AutoValue_TestPackage.Builder.class)
@XmlRootElement(name = "TestPackage")
@XmlType(propOrder={"blueprint", "assessments"})
public abstract class TestPackage {
    @Id
    @XmlTransient
    private String id;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @XmlAttribute
    public abstract String getPublisher();
    @XmlAttribute
    public abstract String getPublishDate();
    @XmlAttribute
    public abstract String getSubject();
    @XmlAttribute
    public abstract String getType();
    @XmlAttribute
    public abstract String getVersion();
    @XmlAttribute
    public abstract int getBankKey();
    @XmlAttribute
    public abstract String getAcademicYear();

    @XmlElementWrapper(name="Blueprint")
    @XmlElement(name="BlueprintElement", type=BlueprintElement.class)
    public abstract List<BlueprintElement> getBlueprint();

    @XmlElement(name="Test", type=Assessment.class)
    public abstract List<Assessment> getAssessments();

    public static Builder builder() {
        return new AutoValue_TestPackage.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setPublisher(String newPublisher);

        public abstract Builder setPublishDate(String newPublishDate);

        public abstract Builder setSubject(String newSubject);

        public abstract Builder setType(String newType);

        public abstract Builder setVersion(String newVersion);

        public abstract Builder setBankKey(int newBankKey);

        public abstract Builder setAcademicYear(String newAcademicYear);

        @JacksonXmlProperty(localName = "Blueprint")
        public abstract Builder setBlueprint(List<BlueprintElement> newBlueprint);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Test")
        public abstract Builder setAssessments(List<Assessment> newAssessments);

        public abstract TestPackage build();
    }

    public Optional<BlueprintElement> getBlueprintElement(final String id) {
        final BlueprintElement blueprintElement = getBlueprintElementHelper(id, this.getBlueprint());
        return blueprintElement != null ? Optional.of(blueprintElement) : Optional.empty();
    }

    public Map<String, BlueprintElement> getBlueprintMap() {
        final List<BlueprintElement> flattenedBlueprintElements = new ArrayList<>();
        getFlatMapBlueprintHelper(flattenedBlueprintElements, this.getBlueprint());

        return flattenedBlueprintElements.stream()
                .collect(Collectors.toMap(BlueprintElement::getId, Function.identity()));
    }

    private BlueprintElement getBlueprintElementHelper(final String id, final List<BlueprintElement> blueprint) {
        for (BlueprintElement bpElement : blueprint) {
            if (bpElement.getId().equalsIgnoreCase(id)){
                return bpElement;
            }

            final BlueprintElement blueprintElement = getBlueprintElementHelper(id, bpElement.blueprintElements());

            if (blueprintElement != null) {
                return blueprintElement;
            }
        }

        return null;
    }

    private void getFlatMapBlueprintHelper(final List<BlueprintElement> flattenedBlueprint,
                                           final List<BlueprintElement> childElements) {
        for (BlueprintElement bpElement : childElements) {
            flattenedBlueprint.add(bpElement);

            if (!bpElement.blueprintElements().isEmpty()) {
                getFlatMapBlueprintHelper(flattenedBlueprint, bpElement.blueprintElements());
            }
        }
    }
}
