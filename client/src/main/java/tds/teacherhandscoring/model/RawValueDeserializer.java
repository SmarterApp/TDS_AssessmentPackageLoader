package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class RawValueDeserializer extends StdDeserializer<RawValue> {

    public RawValueDeserializer() {
        super(RawValue.class);
    }

    @Override
    public RawValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return new RawValue(parser.getCodec().readTree(parser).toString());
    }
}