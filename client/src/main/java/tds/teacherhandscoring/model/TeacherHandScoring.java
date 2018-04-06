package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

import tds.testpackage.model.Item;
import tds.testpackage.model.TestPackage;

/**
 * Teacher Hand Scoring System configuration stored as an element in the Test Package xml file.
 *
 * Deserialize as XML and serialized as JSON to be sent to the THSS server.
 *
 * Used as part of TeacherHandScoringConfiguration during serialization.
 *
 * @see tds.teacherhandscoring.model.TeacherHandScoringConfiguration
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_TeacherHandScoring.Builder.class)
abstract public class TeacherHandScoring {
    protected static String DEFAULT_DIMENSIONS = "[\n" +
        "      {\n" +
        "        \"conditions\": [\n" +
        "          {\n" +
        "            \"code\": \"B\",\n" +
        "            \"description\": \"Blank\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"I\",\n" +
        "            \"description\": \"Insufficient\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"L\",\n" +
        "            \"description\": \"Non-Scorable Language\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"description\": \"CONVENTIONS\",\n" +
        "        \"maxPoints\": \"2\",\n" +
        "        \"minPoints\": \"0\"\n" +
        "      },\n" +
        "      {\n" +
        "        \"conditions\": [\n" +
        "          {\n" +
        "            \"code\": \"B\",\n" +
        "            \"description\": \"Blank\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"I\",\n" +
        "            \"description\": \"Insufficient\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"L\",\n" +
        "            \"description\": \"Non-Scorable Language\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"description\": \"ELABORATION\",\n" +
        "        \"maxPoints\": \"3\",\n" +
        "        \"minPoints\": \"0\"\n" +
        "      },\n" +
        "      {\n" +
        "        \"conditions\": [\n" +
        "          {\n" +
        "            \"code\": \"B\",\n" +
        "            \"description\": \"Blank\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"I\",\n" +
        "            \"description\": \"Insufficient\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"code\": \"L\",\n" +
        "            \"description\": \"Non-Scorable Language\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"description\": \"ORGANIZATION\",\n" +
        "        \"maxPoints\": \"3\",\n" +
        "        \"minPoints\": \"0\"\n" +
        "      }\n" +
        "    ]";

    /**
     * @return location of the PDFs on the THSS server
     */
    public abstract String getBaseUrl();

    /**
     * Note: naming convention is itemId + "_TM.pdf"
     *
     * @return filename of tech manual
     */
    public abstract String getExemplar();

    /**
     * Note: naming convention is itemId + "_SG.pdf"
     *
     * @return filename of scoring guide
     */
    public abstract String getTrainingGuide();


    /**
     * Item content xml path: //rubriclist/rubric/val
     * Initially null after loading from test specification.
     * Populated from the item content retrieved from the content service
     * @return Rubric list found in the item content XML
     */
    @JsonProperty(value = "rubriclist")
    @Nullable
    public abstract String getRubricList();

    /**
     * @return dimensions provided by SmarterBalanced, generally constant
     */
    protected abstract Optional<String> getDimensions();

    /**
     * @return dimensions provided by SmarterBalanced, generally constant
     */
    @JsonRawValue
    public String dimensions() {
        return getDimensions().orElse(DEFAULT_DIMENSIONS);
    }

    /**
     * Item content xml path: itm_item_desc
     * @return description found in the item content XML
     */
    public abstract String getDescription();

    /**
     * Passage identifier used by IRiS to group common items for that passage
     * @return passage identifier
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty(value = "Passage")
    public abstract String getPassage();

    @Nullable
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty(value = "itemname")
    public abstract String getItemName();

    protected abstract Optional<String> getLayout();

    @JsonProperty(value = "layout")
    public String layout() {
        return getLayout().orElse("WAI");
    }

    @Transient
    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @Transient
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

    abstract Builder toBuilder();

    public TeacherHandScoring withRubricList(final String rubricList) {
        final TeacherHandScoring newTeacherHandScoring = toBuilder().setRubricList(rubricList).build();
        newTeacherHandScoring.setItem(getItem());
        newTeacherHandScoring.setTestPackage(getTestPackage());
        return newTeacherHandScoring;
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setBaseUrl(String newBaseUrl);

        public abstract Builder setExemplar(String newExemplar);

        public abstract Builder setTrainingGuide(String newTrainingGuide);

        @JacksonXmlProperty(localName = "Dimensions")
        public abstract Builder setDimensions(Optional<String> newDimensions);

        public Builder setDimensions(String newDimensions) {
            return setDimensions(Optional.ofNullable(newDimensions));
        }

        public abstract Builder setRubricList(String newRubricList);

        public abstract Builder setDescription(String newDescription);

        public abstract Builder setLayout(Optional<String> newLayout);

        public abstract Builder setPassage(String newPassage);

        public abstract Builder setItemName(String newItemName);

        public abstract TeacherHandScoring build();
    }
}
