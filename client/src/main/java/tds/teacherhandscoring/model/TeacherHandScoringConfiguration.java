package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Optional;

import tds.testpackage.model.Item;
import tds.testpackage.model.TestPackage;

/**
 * Teacher hand scoring tds.support.tool.configuration data to be serialized as JSON and
 * sent to the THSS .NET server application endpoint.
 * The JSON file is POSTed to the endpoint as a file attachment.
 * http://host:28039/api/item/submit
 */
public class TeacherHandScoringConfiguration {
    final TeacherHandScoring teacherHandScoring;

    public TeacherHandScoringConfiguration(final TeacherHandScoring teacherHandScoring) {
        this.teacherHandScoring = teacherHandScoring;
    }

    /**
     * Note: naming convention is itemId + "_TM.pdf"
     *
     * @return filename of tech manual
     */
    public Optional<String> getExemplar() {
        return teacherHandScoring.getExemplar();
    }

    /**
     * Note: naming convention is itemId + "_SG.pdf"
     *
     * @return filename of scoring guide
     */
    public Optional<String> getTrainingGuide() {
        return teacherHandScoring.getTrainingGuide();
    }

    /**
     * Item content xml path: //rubriclist/rubric/val
     * Initially null after loading from test specification.
     * Populated from the item content retrieved from the content service
     * @return Rubric list found in the item content XML
     */
    @JsonProperty(value = "rubriclist")
    public RawValue getRubricList() {
        return teacherHandScoring.getRubricList();
    }

    /**
     * @return dimensions provided by SmarterBalanced, generally constant
     */
    @JsonProperty(value = "dimensions")
    public RawValue dimensions() {
        return new RawValue(teacherHandScoring.dimensions());
    }

    /**
     * Item content xml path: itm_item_desc
     * @return description found in the item content XML
     */
    public String getDescription() {
        return teacherHandScoring.getDescription();
    }

    /**
     * Passage identifier used by IRiS to group common items for that passage
     * @return passage identifier
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty(value = "Passage")
    public String getPassage() {
        return teacherHandScoring.getPassage();
    }

    @Nullable
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty(value = "itemname")
    public String getItemName() {
        return teacherHandScoring.getItemName();
    }

    @JsonProperty(value = "layout")
    public String layout() {
        return teacherHandScoring.layout();
    }

    @JsonIgnore
    public Item getItem() {
        return this.teacherHandScoring.getItem();
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.teacherHandScoring.getTestPackage();
    }

    @JsonProperty("itemId")
    public String itemId() {
        return getItem().getId();
    }

    @JsonProperty("bankKey")
    public String bankKey() {
        return String.valueOf(getTestPackage().getBankKey());
    }

    @JsonProperty("HandScored")
    public String handScored() {
        return getItem().handScored() ? "1" : "0";
    }

    @JsonProperty("itemType")
    public String itemType() {
        return getItem().getType();
    }

    @JsonProperty("subject")
    public String subject() {
        return getTestPackage().getSubject();
    }

    @JsonProperty("grade")
    public String grade() {
        return getItem().getItemGroup().getSegment().getAssessment().getGrades().get(0).getValue();
    }
}
