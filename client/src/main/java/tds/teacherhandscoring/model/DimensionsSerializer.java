package tds.teacherhandscoring.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class DimensionsSerializer extends StdSerializer<Dimensions> {

    public DimensionsSerializer() {
        super(Dimensions.class);
    }

    @Override
    public void serialize(Dimensions packet, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (packet.getValue() == null) {
            generator.writeNull();
        } else {
            generator.writeRawValue(packet.getValue());
        }
    }
}