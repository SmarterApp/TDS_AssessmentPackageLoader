package tds.support.tool.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import tds.common.configuration.JacksonObjectMapperConfiguration;
import tds.common.configuration.RestTemplateConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.handlers.loader.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@Configuration
@Import({
    ExceptionAdvice.class,
    JacksonObjectMapperConfiguration.class,
    SecurityConfiguration.class,
    RestTemplateConfiguration.class,
    TestPackageObjectMapperConfiguration.class,
    MvcConfig.class
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
    public Map<String, TestPackageHandler> getTestPackageLoaderStepHandlers(
        final ParseAndValidateHandler parseAndValidateHandler,
        final TDSLoaderStepHandler tdsLoaderStepHandler,
        final ARTLoaderStepHandler artLoaderStepHandler,
        final TISLoaderStepHandler tisLoaderStepHandler,
        final THSSLoaderStepHandler thssLoaderStepHandler,
        final TDSDeleteStepHandler tdsDeleteStepHandler,
        final ARTDeleteStepHandler artDeleteStepHandler,
        final TISDeleteStepHandler tisDeleteStepHandler,
        final THSSDeleteStepHandler thssDeleteStepHandler
        ) {

        final Map<String, TestPackageHandler> handlerMap = new HashMap<>();
        handlerMap.put(TestPackageLoadJob.VALIDATE, parseAndValidateHandler);
        handlerMap.put(TestPackageLoadJob.TDS_UPLOAD, tdsLoaderStepHandler);
        handlerMap.put(TestPackageLoadJob.ART_UPLOAD, artLoaderStepHandler);
        handlerMap.put(TestPackageLoadJob.TIS_UPLOAD, tisLoaderStepHandler);
        handlerMap.put(TestPackageLoadJob.THSS_UPLOAD, thssLoaderStepHandler);
        handlerMap.put(TestPackageDeleteJob.TDS_DELETE, tdsDeleteStepHandler);
        handlerMap.put(TestPackageDeleteJob.ART_DELETE, artDeleteStepHandler);
        handlerMap.put(TestPackageDeleteJob.TIS_DELETE, tisDeleteStepHandler);
        handlerMap.put(TestPackageDeleteJob.THSS_DELETE, thssDeleteStepHandler);

        return handlerMap;
    }

    @Autowired
    ObjectMapper objectMapper;

    @Bean(name = "integrationRestTemplate")
    public RestTemplate restTemplate() {
        // Jackson Converters
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        final List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);
        converters.add(new ResourceHttpMessageConverter());
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
