package tds.support.tool.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;
import tds.common.configuration.JacksonObjectMapperConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.common.web.interceptors.RestTemplateLoggingInterceptor;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.handlers.loader.impl.*;
import tds.support.tool.handlers.scoring.TestResultsHandler;
import tds.support.tool.handlers.scoring.impl.ExamServiceRescoreStepHandler;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import java.util.HashMap;
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
    public Map<String, TestPackageHandler> testPackageLoaderStepHandlers(
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

    @Bean(name = "testResultsLoaderStepHandlers")
    public Map<String, TestResultsHandler> testResultsLoaderStepHandlers(
            final ExamServiceRescoreStepHandler examServiceRescoreStepHandler
    ) {

        final Map<String, TestResultsHandler> handlerMap = new HashMap<>();
        handlerMap.put(TestResultsScoringJob.RESCORE, examServiceRescoreStepHandler);

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

    @Bean(name = "restTemplateBuilder")
    @Primary
    public RestTemplateBuilder restTemplateBuilder(TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration, final ApplicationContext applicationContext) {
        final ObjectMapper objectMapper = getIntegrationObjectMapper();
        final XmlMapper xmlMapper = testPackageObjectMapperConfiguration.getLegacyTestSpecXmlMapper();
        return new RestTemplateBuilder().
            additionalMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper),
                new MappingJackson2XmlHttpMessageConverter(xmlMapper),
                new ResourceHttpMessageConverter()).
            additionalInterceptors(new RestTemplateLoggingInterceptor(objectMapper, applicationContext.getId()));
    }

    @Bean(name = "jaxbRestTemplateBuilder")
    public RestTemplateBuilder jaxbRestTemplateBuilder(final ApplicationContext applicationContext) {
        return new RestTemplateBuilder().
                additionalMessageConverters(
                        new Jaxb2RootElementHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(getIntegrationObjectMapper())).
                additionalInterceptors(new RestTemplateLoggingInterceptor(getIntegrationObjectMapper(), applicationContext.getId()));
    }

    @Primary
    @Bean(name = "integrationRestTemplate")
    public RestTemplate integrationRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.
            requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())).
            build();
    }

    @Bean(name = "jaxbRestTemplate")
    public RestTemplate jaxbRestTemplate(final @Qualifier("jaxbRestTemplateBuilder") RestTemplateBuilder jaxbRestTemplateBuilder) {
        return jaxbRestTemplateBuilder.
                requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())).
                build();
    }

    @Bean
    public Supplier<CloseableHttpClient> httpClientSupplier() {
        return () -> HttpClients.createDefault();
    }

    @Bean
    public OAuth2RestTemplate oAuthRestTemplate(final SupportToolProperties properties) {
        final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(ssoAuthResource(properties));
        for (final HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                    (MappingJackson2HttpMessageConverter) messageConverter;
                mappingJackson2HttpMessageConverter.setObjectMapper(getIntegrationObjectMapper());
                break;
            }
        }
        return restTemplate;
    }

    private OAuth2ProtectedResourceDetails ssoAuthResource(final SupportToolProperties properties) {
        final ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername(properties.getSsoUsername().orElseThrow(() -> new RuntimeException(
            "SSO Username property is not configured")));
        resourceDetails.setPassword(properties.getSsoPassword().orElseThrow(() -> new RuntimeException(
            "SSL Password property is not configured")));
        resourceDetails.setClientId(properties.getSsoClientId().orElseThrow(() -> new RuntimeException(
            "SSL Client ID property is not configured")));
        resourceDetails.setClientSecret(properties.getSsoClientSecret().orElseThrow(() -> new RuntimeException(
            "SSL Client Secret property is not configured")));
        resourceDetails.setAccessTokenUri(properties.getSsoUrl().orElseThrow(() -> new RuntimeException(
            "SSO Url property is not configured")));
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
        return resourceDetails;
    }
}
