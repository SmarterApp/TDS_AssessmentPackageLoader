package tds.testpackage.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

// (SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016
public class ItemXmlDeserializer extends StdDeserializer<Item> {
    public ItemXmlDeserializer() {
        this(null);
    }

    public ItemXmlDeserializer(Class<Item> vc) {
        super(vc);
    }

    public Item deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final FromXmlParser jp = (FromXmlParser) p;
        final XmlMapper xmlMapper = (XmlMapper) jp.getCodec();
        XMLStreamReader staxReader = jp.getStaxReader();
        String id = null;
        String type = null;
        Optional<Integer> position = Optional.empty();
        List<BlueprintReference> blueprintReferences = null;
        List<String> presentations = null;
        ItemScoreDimension itemScoreDimension = null;
        String clientId = jp.getParsingContext().getParent().getParent()
        for (; jp.getCurrentToken() != JsonToken.END_OBJECT
            || !"Item".equals(jp.getStaxReader().getLocalName()); jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("id".equals(jp.getCurrentName())) {
                    id = jp.nextTextValue();
                } else if ("type".equals(jp.getCurrentName())) {
                    type = jp.nextTextValue();
                } else if ("position".equals(jp.getCurrentName())) {
                    position = Optional.of(jp.nextIntValue(0));
                } else if ("BlueprintReferences".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    blueprintReferences = xmlMapper.readValue(jp, new TypeReference<List<BlueprintReference>>() {
                    });
                } else if ("ItemScoreDimension".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    itemScoreDimension = xmlMapper.readValue(jp, ItemScoreDimension.class);
                } else if ("Presentations".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    presentations = xmlMapper.readValue(jp, new TypeReference<List<String>>() {
                    });
                }
            }
        }

        Item item = Item.builder()
            .setPosition(position)
            .setPresentations(presentations)
            .setBlueprintReferences(blueprintReferences)
            .setId(id)
            .setType(type)
            .setItemScoreDimension(itemScoreDimension)
            .build();

        return item;
    }
}
