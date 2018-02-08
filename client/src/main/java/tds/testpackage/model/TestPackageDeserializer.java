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
        String publisher = null;
        String publishDate = null;
        String subject = null;
        String type = null;
        String version = null;
        int bankKey = 0;
        String academicYear = null;
        List<BlueprintElement> blueprint = null;
        List<Assessment> assessments = new ArrayList<>();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("publisher".equals(jp.getCurrentName())) {
                    publisher = jp.nextTextValue();
                } else if ("publishDate".equals(jp.getCurrentName())) {
                    publishDate = jp.nextTextValue();
                } else if ("subject".equals(jp.getCurrentName())) {
                    subject = jp.nextTextValue();
                } else if ("type".equals(jp.getCurrentName())) {
                    type =  jp.nextTextValue();
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
                } else if ("Assessment".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    assessments.add(xmlMapper.readValue(jp, Assessment.class));
                } else if ("assessments".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    assessments = xmlMapper.readValue(jp, new TypeReference<List<Assessment>>() {});
                }
            }
        }

        final TestPackage testPackage = TestPackage.builder().
            setPublisher(publisher).
            setPublishDate(publishDate).
            setSubject(subject).
            setType(type).
            setVersion(version).
            setBankKey(bankKey).
            setAcademicYear(academicYear).
            setBlueprint(blueprint).
            setAssessments(assessments).
            build();

        assessments.forEach(assessment -> {
            assessment.setTestPackage(testPackage);
            assessment.getSegments().forEach(segment -> {
                segment.setAssessment(assessment);
                segment.segmentForms().forEach(segmentForm -> {
                    segmentForm.setSegment(segment);
                    segmentForm.itemGroups().forEach(itemGroup -> setItemGroup(itemGroup, testPackage, segment, Optional.of(segmentForm)));

                });
                segment.pool().forEach(itemGroup -> setItemGroup(itemGroup, testPackage, segment, Optional.empty()));
            });
        });

        return testPackage;
    }

    private static void setItemGroup(final ItemGroup itemGroup, final TestPackage testPackage, final Segment segment, final Optional<SegmentForm> segmentForm) {
        itemGroup.setSegment(segment);
        itemGroup.getItems().forEach(item -> {
            item.setTestPackage(testPackage);
            item.setItemGroup(itemGroup);
            item.setSegmentForm(segmentForm);
        });
        itemGroup.getStimulus().ifPresent(
            stimulus -> stimulus.setItemGroup(itemGroup));
    }
}
