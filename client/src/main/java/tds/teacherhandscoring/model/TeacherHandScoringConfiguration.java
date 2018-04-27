package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import tds.testpackage.model.Item;
import tds.testpackage.model.TestPackage;

/**
 * Teacher hand scoring configuration data to be serialized as JSON and
 * sent to the THSS .NET server application endpoint.
 * The JSON file is POSTed to the endpoint as a file attachment.
 * http://host:28039/api/item/submit
 */
public class TeacherHandScoringConfiguration {
    @JsonUnwrapped
    final TeacherHandScoring teacherHandScoring;

    public TeacherHandScoringConfiguration(final TeacherHandScoring teacherHandScoring) {
        this.teacherHandScoring = teacherHandScoring;
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
