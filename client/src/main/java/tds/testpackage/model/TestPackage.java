package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * The test package.
 * contains an optional <Scoring> element (for test packages that contain COMBINED scoring data, such as ICA assessments).
 */
@AutoValue
@JsonDeserialize(using = TestPackageDeserializer.class,  builder = AutoValue_TestPackage.Builder.class)
public abstract class TestPackage {
    public abstract String getPublisher();
    public abstract String getPublishDate();
    public abstract String getSubject();
    public abstract String getType();
    public abstract String getVersion();
    public abstract int getBankKey();
    public abstract String getAcademicYear();
    public abstract List<BlueprintElement> getBlueprint();
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
        @JacksonXmlProperty(localName = "Assessment")
        public abstract Builder setAssessments(List<Assessment> newAssessments);

        public abstract TestPackage build();
    }
}
