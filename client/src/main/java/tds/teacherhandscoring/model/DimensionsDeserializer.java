package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class DimensionsDeserializer extends StdDeserializer<Dimensions> {

    public DimensionsDeserializer() {
        super(Dimensions.class);
    }

    @Override
    public Dimensions deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return new Dimensions(parser.getCodec().readTree(parser).toString());
    }
}