package tds.testpackage.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestPackageDeserializer extends StdDeserializer<TestPackage> {
    public TestPackageDeserializer() {
        super(TestPackage.class);
    }

    @Override
    public TestPackage deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectMapper xmlMapper = (ObjectMapper) jp.getCodec();
        String id = null;
        String publisher = null;
        String publishDate = null;
        String subject = null;
        String type = null;
        String version = null;
        String subType = null;
        int bankKey = 0;
        String academicYear = null;
        List<BlueprintElement> blueprint = null;
        List<Assessment> assessments = new ArrayList<>();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("id".equals(jp.getCurrentName())) {
                    id = jp.nextTextValue();
                } else if ("publisher".equals(jp.getCurrentName())) {
                    publisher = jp.nextTextValue();
                } else if ("publishDate".equals(jp.getCurrentName())) {
                    publishDate = jp.nextTextValue();
                } else if ("subject".equals(jp.getCurrentName())) {
                    subject = jp.nextTextValue();
                } else if ("type".equals(jp.getCurrentName())) {
                    type =  jp.nextTextValue();
                } else if ("subType".equals(jp.getCurrentName())) {
                    subType =  jp.nextTextValue();
                } else if ("version".equals(jp.getCurrentName())) {
                    version = jp.nextTextValue();
                } else if ("bankKey".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    bankKey = Integer.parseInt(jp.getValueAsString());
                } else if ("academicYear".equals(jp.getCurrentName())) {
                    academicYear = jp.nextTextValue();
                } else if ("blueprint".equals(jp.getCurrentName().toLowerCase())) {
                    jp.nextToken();
                    blueprint = xmlMapper.readValue(jp, new TypeReference<List<BlueprintElement>>() {});
                } else if ("Test".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    assessments.add(xmlMapper.readValue(jp, Assessment.class));
                } else if ("assessments".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    assessments = xmlMapper.readValue(jp, new TypeReference<List<Assessment>>() {});
                }
            }
        }
        
        setBlueprintElementParent(blueprint);

        final TestPackage testPackage = TestPackage.builder().
            setId(id).
            setPublisher(publisher).
            setPublishDate(publishDate).
            setSubject(subject).
            setType(type).
            setSubType(subType != null ? Optional.of(subType) : Optional.empty()).
            setVersion(version).
            setBankKey(bankKey).
            setAcademicYear(academicYear).
            setBlueprint(blueprint).
            setAssessments(assessments).
            build();

        setTestPackageParent(testPackage);

        return testPackage;
    }

    /**
     * Utility method to assign the parent object reference to child objects.
     * This allows for nested children objects to access values stored in the parent.
     * 
     * @param testPackage
     */
    public static void setTestPackageParent(final TestPackage testPackage) {
        testPackage.getAssessments().forEach(assessment -> {
            assessment.setTestPackage(testPackage);
            assessment.getSegments().forEach(segment -> {
                segment.setAssessment(assessment);
                segment.segmentForms().forEach(segmentForm -> {
                    segmentForm.setSegment(segment);
                    segmentForm.itemGroups().forEach(itemGroup -> setItemGroup(itemGroup, testPackage, segment, segmentForm));

                });
                segment.pool().forEach(itemGroup -> setItemGroup(itemGroup, testPackage, segment, null));
            });
        });
    }

    private static void setItemGroup(final ItemGroup itemGroup, final TestPackage testPackage, final Segment segment, final SegmentForm segmentForm) {
        itemGroup.setSegment(segment);
        itemGroup.getItems().forEach(item -> {
            item.setTestPackage(testPackage);
            item.setSegmentForm(segmentForm);
            item.setItemGroup(itemGroup);
            item.getTeacherHandScoring().ifPresent(
                teacherHandScoring -> {
                    teacherHandScoring.setItem(item);
                    teacherHandScoring.setTestPackage(testPackage);
                });
        });
        itemGroup.getStimulus().ifPresent(
            stimulus -> stimulus.setTestPackage(testPackage));
    }

    private static void setBlueprintElementParent(final List<BlueprintElement> blueprintElements) {
        setBlueprintElementParentHelper(blueprintElements, null);
    }

    private static void setBlueprintElementParentHelper(final List<BlueprintElement> childElements,
                                                        final BlueprintElement parentElement) {
        for (BlueprintElement childEl : childElements) {
            childEl.setParentBlueprintElement(parentElement);
            setBlueprintElementParentHelper(childEl.blueprintElements(), childEl);
        }
    }
}
