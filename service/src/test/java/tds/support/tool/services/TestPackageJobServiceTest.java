package tds.support.tool.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.InputStream;
import java.util.List;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.impl.SupportToolTestConfiguration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Runs the support tool load package job.
 * Mocks external services: (MongoDB, Microservices, THSS)
 *
 * Excludes spring from auto configuring RabbitMQ.
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class TestPackageJobServiceTest {
    /**
     * THSS Service uses Apache HTTP Client, which is not as
     * convenient to mock as the Spring Rest Template.
     */
    @TestConfiguration
    @Import({SupportToolTestConfiguration.class})
    public static class TestPackageJobServiceConfiguration {
    }

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private TestPackageJobService testPackageJobService;
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
        InputStream inputStream = TestPackageJobServiceTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        Job job = testPackageJobService.startPackageImport(filename, inputStream, -1, true, false);

        // rabbitmq not running, call the job service manually
        testPackageJobService.executeJobSteps(job.getId());

        List<Job> rollbackJobs = testPackageJobService.findJobs(JobType.ROLLBACK);
        assertThat(rollbackJobs.size(), equalTo(0));
    }


    @Test
    @WithMockUser(username = "admin", authorities = { "PERM_SUPPORT_TOOL_ADMINISTRATION"})
    public void shouldRollbackWhenTHSSReturnsError() {
        String filename = "thss-test-specification-error-example.xml";
        InputStream inputStream = TestPackageJobServiceTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        Job job = testPackageJobService.startPackageImport(filename, inputStream, -1, true, false);


        // rabbitmq not running, call the job service manually
        testPackageJobService.executeJobSteps(job.getId());

        testPackageJobService.findJobs(JobType.ROLLBACK).size();

        List<Job> rollbackJobs = testPackageJobService.findJobs(JobType.ROLLBACK);
        assertThat(rollbackJobs.size(), equalTo(1));
    }

}
