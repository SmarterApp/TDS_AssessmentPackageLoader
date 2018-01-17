package tds.testpackage.model;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.io.IOException;

public class ItemXmlDelegatingDeserializer extends StdNodeBasedDeserializer<Item> {
    public  ItemXmlDelegatingDeserializer() {
        super(Item.class);
    }

    @Override
    public Item convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        String id = root.path("id").asText();
        String type = root.path("type").asText();
        return Item.builder().setId(id).setType(type).build();
    }
}


