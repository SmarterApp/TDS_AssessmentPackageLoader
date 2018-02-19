package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Used the the THSS json object mapper.
 * Serializing rubrics and sampleList separately
 * and joining them together as children of RubricList.
 */
public class RubricListSerializer extends StdSerializer<RubricList> {
    final XmlMapper xmlMapper;

    public RubricListSerializer(final XmlMapper xmlMapper) {
        super(RubricList.class);
        this.xmlMapper = xmlMapper;
    }

    @Override
    public void serialize(RubricList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        final String rubricOrSampleList = IntStream.
                range(0, Math.min(value.getRubrics().size(), value.getSampleLists().size())).
                mapToObj(i -> {
                    String json = "";
                    try {
                        final String rubric = xmlMapper.writeValueAsString(value.getRubrics().get(i));
                        final String sampleList = xmlMapper.writeValueAsString(value.getSampleLists().get(i));
                        json = String.format("%s\n%s\n", rubric, sampleList);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return json;
                }).collect(Collectors.joining());

        jgen.writeString(String.format("<rubriclist>\n%s</rubriclist>", rubricOrSampleList));
    }
}
