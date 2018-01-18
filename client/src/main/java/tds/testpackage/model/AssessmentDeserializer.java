package tds.testpackage.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.io.IOException;
import java.util.List;

public class AssessmentDeserializer extends StdDeserializer<Assessment> {
    public AssessmentDeserializer() {
        super(Assessment.class);
    }

    @Override
    public Assessment deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectMapper xmlMapper = (ObjectMapper) jp.getCodec();
        String id = null;
        String label = null;
        List<Grade> grades = null;
        List<Segment> segments = null;
        List<Tool> tools = null;

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("id".equals(jp.getCurrentName())) {
                    id = jp.nextTextValue();
                } else if ("label".equals(jp.getCurrentName())) {
                    label = jp.nextTextValue();
                } else if ("grades".equals(jp.getCurrentName().toLowerCase())) {
                    jp.nextToken();
                    grades = xmlMapper.readValue(jp, new TypeReference<List<Grade>>() {});
                } else if ("segments".equals(jp.getCurrentName().toLowerCase())) {
                    jp.nextToken();
                    segments = xmlMapper.readValue(jp, new TypeReference<List<Segment>>() {});
                } else if ("tools".equals(jp.getCurrentName().toLowerCase())) {
                    jp.nextToken();
                    tools = xmlMapper.readValue(jp, new TypeReference<List<Tool>>() {});
                }
            }
        }

        Assessment assessment = Assessment.builder()
            .setId(id)
            .setLabel(label)
            .setGrades(grades)
            .setSegments(segments)
            .setTools(tools)
            .build();

        segments.stream().forEach(s -> s.setAssessment(assessment));

        return assessment;
    }
}
