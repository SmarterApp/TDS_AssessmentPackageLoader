package tds.support.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tds.teacherhandscoring.model.RubricList;
import tds.teacherhandscoring.model.RubricListSerializer;

@Configuration
public class TestPackageObjectMapperConfiguration {
    @Bean(name = "xmlMapper")
    public XmlMapper getXmlMapper() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new Jdk8Module());
        return xmlMapper;
    }

    /**
     * Specialized json serializer
     * used to post data to the THSS server.
     * The rubric list field contains xml data with unusual data structures.
     * The RubricListSerializer handles these special cases.
     * @return
     */
    @Bean(name = "thssObjectMapper")
    public ObjectMapper getThssObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        final SimpleModule module = new SimpleModule();
        module.addSerializer(RubricList.class, new RubricListSerializer(getXmlMapper()));
        objectMapper.registerModule(module);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }

    @Primary
    @Bean
    public ObjectMapper getObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }
}
