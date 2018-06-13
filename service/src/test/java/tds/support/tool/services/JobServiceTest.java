package tds.support.tool.services;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Arrays;
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
import tds.support.tool.services.impl.SupportToolTestConfiguration;
import tds.support.tool.services.impl.THSSServiceImpl;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Runs the support tool load package job.
 * Mocks external services: (MongoDB, Microservices, THSS)
 *
 * Excludes spring from auto configuring RabbitMQ.
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class JobServiceTest {
    /**
     * THSS Service uses Apache HTTP Client, which is not as
     * convenient to mock as the Spring Rest Template.
     */
    @TestConfiguration
    @Import({SupportToolTestConfiguration.class})
    public static class JobServiceConfiguration {
    }

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobService jobService;
    @Autowired
    private SupportToolTestConfiguration supportToolTestConfiguration;

    private MockRestServiceServer mockServer;

    @Before
    public void setup() throws Exception {
        mockServer = supportToolTestConfiguration.setUpMockServer();
    }

    @After
    public void clear() {
        jobRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "PERM_SUPPORT_TOOL_ADMINISTRATION"})
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
    @WithMockUser(username = "admin", authorities = { "PERM_SUPPORT_TOOL_ADMINISTRATION"})
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
