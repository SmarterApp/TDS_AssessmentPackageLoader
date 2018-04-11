package tds.support.tool.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import tds.common.configuration.JacksonObjectMapperConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.common.web.interceptors.RestTemplateLoggingInterceptor;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.handlers.loader.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@EnableAsync
@Configuration
@Import({
    ExceptionAdvice.class,
    SecurityConfiguration.class,
    JacksonObjectMapperConfiguration.class,
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

    private ObjectMapper getIntegrationObjectMapper() {
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new GuavaModule())
            .registerModule(new JodaModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(final ApplicationContext applicationContext) {
        final ObjectMapper objectMapper = getIntegrationObjectMapper();
        return new RestTemplateBuilder().
            additionalMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper),
                new MappingJackson2XmlHttpMessageConverter(),
                new ResourceHttpMessageConverter()).
            additionalInterceptors(new RestTemplateLoggingInterceptor(objectMapper, applicationContext.getId())).
            additionalMessageConverters();
    }

    @Bean(name = "integrationRestTemplate")
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.
            requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())).
            build();
    }

    @Bean
    public Supplier<CloseableHttpClient> httpClientSupplier() {
        return () -> HttpClients.createDefault();
    }
}
