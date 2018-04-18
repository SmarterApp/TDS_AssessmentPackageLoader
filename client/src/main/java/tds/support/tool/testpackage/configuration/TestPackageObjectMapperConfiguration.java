package tds.support.tool.testpackage.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

@Component
public class TestPackageObjectMapperConfiguration {
    private static final String TEST_PACKAGE_SCHEMA_PATH = "/xsd/v4-test-package.xsd";

    public XmlMapper getXmlMapper() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new Jdk8Module());
        return xmlMapper;
    }

    public XmlMapper getLegacyTestSpecXmlMapper() {
        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        final XmlMapper xmlMapper = new XmlMapper(xmlModule);
        xmlMapper.registerModule(new Jdk8Module());
        xmlMapper.registerModule(new JaxbAnnotationModule());
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return xmlMapper;
    }

    /**
     * Specialized json serializer
     * used to post data to the THSS server.
     */
    public ObjectMapper getThssObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }

    public Validator getTestPackageSchemaValidator() throws SAXException {
        Source schemaSource = new StreamSource(this.getClass().getResourceAsStream(TEST_PACKAGE_SCHEMA_PATH));
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(schemaSource);
        return schema.newValidator();
    }
}
