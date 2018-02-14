package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RubricListSerializer extends StdSerializer<RubricList> {
    final XmlMapper xmlMapper;

    public RubricListSerializer(final XmlMapper xmlMapper) {
        super(RubricList.class);
        this.xmlMapper = xmlMapper;
    }


    @Override
    public void serialize(RubricList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
//        jgen.writeStartObject();

        String rubric = xmlMapper.writeValueAsString(value.rubricOrSamplelist().get(0));
        String sampleList = xmlMapper.writeValueAsString(value.rubricOrSamplelist().get(1));
        jgen.writeString(String.format("<rubriclist>%s%s</rubriclist>", rubric, sampleList));

//        jgen.writeEndObject();
    }
}
