package tds.support.tool.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

import tds.common.configuration.JacksonObjectMapperConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;

@EnableAsync
@Configuration
@Import({
    ExceptionAdvice.class,
    JacksonObjectMapperConfiguration.class,
    SecurityConfiguration.class,
})
public class SupportToolServiceConfiguration {
    @Bean
    public AmazonS3 getAmazonS3(final S3Properties s3Properties) {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .withRegion(Regions.US_WEST_2)
            .build();
    }

    @Bean(name = "testPackageLoaderStepHandlers")
    public Map<String, TestPackageHandler> getTestPackageLoaderStepHandlers() {
        return new HashMap<>();
    }

    @Bean
    public XmlMapper getXmlMapper() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new Jdk8Module());
        return xmlMapper;
    }
}
