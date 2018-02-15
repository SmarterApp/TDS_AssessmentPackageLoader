package tds.support.tool.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

import tds.common.configuration.JacksonObjectMapperConfiguration;
import tds.common.configuration.RestTemplateConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.handlers.loader.impl.ARTDeleteStepHandler;
import tds.support.tool.handlers.loader.impl.ARTLoaderStepHandler;
import tds.support.tool.handlers.loader.impl.ParseAndValidateHandler;
import tds.support.tool.handlers.loader.impl.TDSDeleteStepHandler;
import tds.support.tool.handlers.loader.impl.TDSLoaderStepHandler;
import tds.support.tool.handlers.loader.impl.THSSDeleteStepHandler;
import tds.support.tool.handlers.loader.impl.THSSLoaderStepHandler;
import tds.support.tool.handlers.loader.impl.TISDeleteStepHandler;
import tds.support.tool.handlers.loader.impl.TISLoaderStepHandler;

@EnableAsync
@Configuration
@Import({
    ExceptionAdvice.class,
    JacksonObjectMapperConfiguration.class,
    SecurityConfiguration.class,
    RestTemplateConfiguration.class,
    TestPackageObjectMapperConfiguration.class
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
}
