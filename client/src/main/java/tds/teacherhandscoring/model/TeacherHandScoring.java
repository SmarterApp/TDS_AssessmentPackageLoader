package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Optional;

import tds.testpackage.model.Item;
import tds.testpackage.model.ItemGroup;
import tds.testpackage.model.TestPackage;

@AutoValue
@JsonDeserialize(builder = AutoValue_TeacherHandScoring.Builder.class)
abstract public class TeacherHandScoring {
    public abstract String getBaseUrl();

    public abstract String getExemplar();

    public abstract String getTrainingGuide();

    public abstract RubricList getRubricList();

    @JsonRawValue
    public abstract String getDimensions();

    public abstract String getDescription();

    protected abstract Optional<String> getLayout();

    @JsonProperty(value = "layout")
    public String layout() {
        return getLayout().orElse("WAI");
    }

    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    private Item item;

    public void setItem(Item item) {
        this.item = item;
    }

    @JsonIgnore
    public Item getItem() {
        return this.item;
    }

    public static Builder builder() {
        return new AutoValue_TeacherHandScoring.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setBaseUrl(String newBaseUrl);

        public abstract Builder setExemplar(String newExemplar);

        public abstract Builder setTrainingGuide(String newTrainingGuide);

        @JacksonXmlProperty(localName = "rubriclist")
        public abstract Builder setRubricList(RubricList newRubricList);

        @JacksonXmlProperty(localName = "Dimensions")
        public abstract Builder setDimensions(String newDimensions);

        public abstract Builder setDescription(String newDescription);

        public abstract Builder setLayout(Optional<String> newLayout);

        public abstract TeacherHandScoring build();
    }
}
