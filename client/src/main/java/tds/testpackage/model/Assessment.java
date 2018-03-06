package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;


/**
 * The Assessment node contains the following attributes:
 *   key: The assessment key
 *   id: The assessment id
 *   publisher: The publisher of the test package, also referred to as the "client name"
 *   publishDate: The creation/publish date of the test package
 *   label: A human-readable label to be displayed in the student and proctor application
 *   subject: The subject of the assessment (e.g., "ELA", "MATH", "Student Help")
 *   type: The test type of the assessment (e.g., "interim", "summative")
 *   version: The version of the test specification
 *   bankKey: The item bank key for all forms and items, typically a 3-digit integer value (e.g., "187", "200")
 *   subType: (OPTIONAL) A subtype classification of the exam (e.g., "ICA", "IAB")
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Assessment.Builder.class)
public abstract class Assessment {
    @XmlAttribute
    public abstract String getId();
    @XmlAttribute
    public abstract String getLabel();

    @XmlElementWrapper(name="Grades")
    @XmlElement(name="Grade", type=Grade.class)
    public abstract List<Grade> getGrades();

    @XmlElementWrapper(name="Segments")
    @XmlElement(name="Segment", type=Segment.class)
    public abstract List<Segment> getSegments();

    @XmlElementWrapper(name="Tools")
    @XmlElement(name="Tool", type=Tool.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract List<Tool> getTools();

    @JsonIgnore
    public boolean isSegmented() {
        return getSegments().size() > 1;
    }

    @Transient
    @XmlTransient
    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @JsonIgnore
    public String getKey() {
        return String.format("(%s)%s-%s", testPackage.getPublisher(), getId(), testPackage.getAcademicYear());
    }

    public static Builder builder() {
        return new AutoValue_Assessment.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Builder setLabel(String newLabel);

        @JacksonXmlProperty(localName = "Grades")
        public abstract Builder setGrades(List<Grade> newGrades);

        @JacksonXmlProperty(localName = "Segments")
        public abstract Builder setSegments(List<Segment> newSegments);

        @JacksonXmlProperty(localName = "Tools")
        public abstract Builder setTools(List<Tool> newTools);

        public abstract Assessment build();
    }
}
