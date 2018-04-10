package tds.support.tool.services;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.configuration.SupportToolServiceConfiguration;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.services.impl.JobServiceImpl;
import tds.support.tool.services.impl.THSSServiceImpl;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@Configuration
@ComponentScan(basePackages = {
    "tds.support.tool.testpackage.configuration",
    "tds.support.tool.handlers.loader",
    "tds.support.tool.services.loader",
    "tds.support.tool.handlers.loader.impl",
    "tds.support.tool.services.impl",
    "tds.support.tool.validation"})
@Import({SupportToolServiceConfiguration.class, SupportToolProperties.class})
@DataMongoTest
/**
 * Runs the support tool load package job.
 * Mocks external services: (MongoDB, Microservices, THSS)
 *
 * Excludes spring from auto configuring RabbitMQ.
 */
public class JobServiceTest {
    /**
     * THSS Service uses Apache HTTP Client, which is not as
     * convenient to mock as the Spring Rest Template.
     */
    public static class JobServiceConfiguration {
        @Autowired
        SupportToolProperties supportToolProperties;
        @Autowired
        TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;
        @Autowired
        RestTemplate restTemplate;

        @Bean
        @Primary
        public THSSService mockService()
        {
            // Returns an Http Client that conditionally returns different json responses
            // depending on the contents of the http request body.

            Supplier<CloseableHttpClient> httpClientSupplier = () -> {
                HttpClientMock httpClientMock = new HttpClientMock();
                httpClientMock.onPost("http://localhost:28039/api/item/submit").
                    withBody(containsString("success")).
                    doReturnJSON("{\n" +
                    "    \"Files\": [\n" +
                    "        {\n" +
                    "            \"FileName\": \"thss-example.json\",\n" +
                    "            \"Success\": true,\n" +
                    "            \"ErrorMessage\": null\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");

                httpClientMock.onPost("http://localhost:28039/api/item/submit").
                    withBody(containsString("error")).
                    doReturnJSON("{\n" +
                    "    \"Files\": [\n" +
                    "        {\n" +
                    "            \"FileName\": \"thss-example.json\",\n" +
                    "            \"Success\": false,\n" +
                    "            \"ErrorMessage\": \"Mock Error Message\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");

                return httpClientMock;
            };

            return new THSSServiceImpl("/tds/bank/items/Item-%1$s-%2$s/item-%1$s-%2$s.xml", "", httpClientSupplier, supportToolProperties, restTemplate, testPackageObjectMapperConfiguration);
        }
    }

    @Autowired
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private TestPackageFileHandler testPackageFileHandler;
    @Qualifier("testPackageLoaderStepHandlers")
    @Autowired
    private Map<String, TestPackageHandler> testPackageLoaderStepHandlers;
    @Autowired
    private TestPackageStatusService testPackageStatusService;
    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    AmazonS3 amazonS3;
    @MockBean
    TestPackageRepository testPackageRepository;
    @MockBean
    MessagingService messagingService;

    JobService jobService;
    MockRestServiceServer mockServer;

    static String RUBRIC_LIST =
        "<rubriclist>\n" +
        "   <rubric scorepoint=\"4\">\n" +
        "       <name>        Rubric 4</name>\n" +
        "       <val>\n" +
        "           <![CDATA[<p style=\"\">&#xA0;</p><p><dl><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">a)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Inference here.</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">b)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Text-supported example: “....” (paragraph reference)</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">c)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Inference here.</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">d)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Text-supported example: “....” (paragraph reference)</p></dd></dl></p>]]>\n" +
        "       </val>\n" +
        "   </rubric>\n" +
        "   <samplelist maxval=\"4\" minval=\"4\">\n" +
        "       <sample purpose=\"OtherExemplar\" scorepoint=\"4\">\n" +
        "           <name>4-Point Other Official Sample Answers      </name>\n" +
        "           <annotation />\n" +
        "           <samplecontent>\n" +
        "               <![CDATA[<p style=\"\">&#xA0;</p>]]>\n" +
        "           </samplecontent>\n" +
        "       </sample>\n" +
        "   </samplelist>\n" +
        "</rubriclist>";


    @Before
    public void setup() throws Exception {
        ObjectMapper objectMapper = testPackageObjectMapperConfiguration.getThssObjectMapper();
        jobService = new JobServiceImpl(jobRepository, testPackageFileHandler, messagingService, testPackageStatusService, testPackageLoaderStepHandlers);
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        String jsonResponse = objectMapper.writeValueAsString(Optional.of(RUBRIC_LIST));
        mockServer.expect(manyTimes(), method(HttpMethod.GET)).andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON_UTF8));
        mockServer.expect(manyTimes(), method(HttpMethod.POST)).andRespond(withSuccess());
    }

    @After
    public void clear() {
        jobRepository.deleteAll();
    }

    @Test
    public void shouldLoadTestSpecificationPackage() {
        String filename = "thss-test-specification-success-example.xml";
        InputStream inputStream = JobServiceTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        Job job = jobService.startPackageImport(filename, inputStream, -1, true, false);

        // rabbitmq not running, call the job service manually
        jobService.executeJobSteps(job.getId());

        List<Job> rollbackJobs = jobService.findJobs(JobType.ROLLBACK);
        assertThat(rollbackJobs.size(), equalTo(0));
    }


    @Test
    public void shouldRollbackWhenTHSSReturnsError() {
        String filename = "thss-test-specification-error-example.xml";
        InputStream inputStream = JobServiceTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        Job job = jobService.startPackageImport(filename, inputStream, -1, true, false);


        // rabbitmq not running, call the job service manually
        jobService.executeJobSteps(job.getId());

        jobService.findJobs(JobType.ROLLBACK).size();

        List<Job> rollbackJobs = jobService.findJobs(JobType.ROLLBACK);
        assertThat(rollbackJobs.size(), equalTo(1));
    }

}
