package tds.testpackage.diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import tds.testpackage.model.TestPackageDeserializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@AutoValue
@XmlRootElement(name = "TestPackageDiff")
@JsonDeserialize(using = TestPackageDeserializer.class,  builder = AutoValue_TestPackageDiff.Builder.class)
@XmlType(propOrder={"assessments"})
public abstract class TestPackageDiff {
    @XmlElement(name="Test", type=Assessment.class)
    public abstract List<Assessment> getAssessments();

    @XmlAttribute
    public abstract int getBankKey();

    @XmlAttribute
    public abstract String getAcademicYear();

    @XmlAttribute
    public abstract String getSubType();

    public static Builder builder() {
        return new AutoValue_TestPackageDiff.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setSubType(String newSubType);

        public abstract Builder setBankKey(int newBankKey);

        public abstract Builder setAcademicYear(String newAcademicYear);

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Test")
        public abstract Builder setAssessments(List<Assessment> newAssessments);

        public abstract TestPackageDiff build();
    }
}
