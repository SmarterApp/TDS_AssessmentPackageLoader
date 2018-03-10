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

import tds.teacherhandscoring.model.RubricList;
import tds.teacherhandscoring.model.RubricListSerializer;

@Component
public class TestPackageObjectMapperConfiguration {
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
        final XmlMapper xmlMapper = getXmlMapper();

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        final SimpleModule module = new SimpleModule();
        module.addSerializer(RubricList.class, new RubricListSerializer(xmlMapper));
        objectMapper.registerModule(module);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }
}
