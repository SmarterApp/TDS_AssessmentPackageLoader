package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RubricListDeserializer extends StdDeserializer<RubricList> {
    public RubricListDeserializer() {
        super(RubricList.class);
    }

    @Override
    public RubricList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectMapper xmlMapper = (ObjectMapper) jp.getCodec();
        List<Rubric> rubrics = new ArrayList<>();
        List<SampleList> sampleList = new ArrayList<>();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("rubric".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    rubrics.add(xmlMapper.readValue(jp, Rubric.class));
                } else if ("samplelist".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    sampleList.add(xmlMapper.readValue(jp, SampleList.class));
                }
            }
        }


        final List<Object> rubricOrSampleList = Stream.concat(rubrics.stream(), sampleList.stream()).
            collect(Collectors.toList());

        RubricList rubricList = RubricList.builder().
            rubricOrSamplelist(rubricOrSampleList).
            build();

        return rubricList;
    }
}
