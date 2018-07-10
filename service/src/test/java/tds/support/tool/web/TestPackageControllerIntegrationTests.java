package tds.support.tool.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.JobType;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.services.TestPackageJobService;

import java.net.URI;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TestPackageControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @MockBean
    private TestPackageJobService mockTestPackageJobService;

    @Test
    public void testFindLoaderJobs() throws Exception {
        List mockJobs = randomListOf(3, TestPackageLoadJob.class);
        when(mockTestPackageJobService.findJobs(JobType.LOAD)).thenReturn(mockJobs);

        http.perform(get(new URI("/api/load"))
                .param("jobType", "LOAD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestPackageJobService).findJobs(JobType.LOAD);
    }

    @Test
    public void testFindAllJobs() throws Exception {
        List mockJobs = randomListOf(3, TestPackageLoadJob.class);
        when(mockTestPackageJobService.findJobs(null)).thenReturn(mockJobs);

        http.perform(get(new URI("/api/load"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestPackageJobService).findJobs(null);
    }

    @Test
    public void shouldStartADeletePackageJob() throws Exception {
        final String testPackageName = "delete-me";

        doNothing().when(mockTestPackageJobService).startPackageDelete(testPackageName);

        http.perform(delete(String.format("/api/load/%s", testPackageName))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(mockTestPackageJobService).startPackageDelete(testPackageName);
    }
}
