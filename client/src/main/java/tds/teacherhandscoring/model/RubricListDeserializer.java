package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rubric list element contains children that can either be Rubric or SampleList.
 * Adding Rubrics and SampleLists into their own, separate lists in the Rubric.
 */
public class RubricListDeserializer extends StdDeserializer<RubricList> {
    public RubricListDeserializer() {
        super(RubricList.class);
    }

    @Override
    public RubricList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final ObjectMapper xmlMapper = (ObjectMapper) jp.getCodec();
        final List<Rubric> rubrics = new ArrayList<>();
        final List<SampleList> sampleLists = new ArrayList<>();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("rubric".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    rubrics.add(xmlMapper.readValue(jp, Rubric.class));
                } else if ("samplelist".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    sampleLists.add(xmlMapper.readValue(jp, SampleList.class));
                }
            }
        }

        return RubricList.builder().
            setRubrics(rubrics).
            setSampleLists(sampleLists).
            build();
    }
}
