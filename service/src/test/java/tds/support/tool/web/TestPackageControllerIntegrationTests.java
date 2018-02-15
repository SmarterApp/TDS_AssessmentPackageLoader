package tds.support.tool.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;

import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.JobType;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.services.JobService;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TestPackageControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService mockJobService;

    @Test
    public void testFindLoaderJobs() throws Exception {
        List mockJobs = randomListOf(3, TestPackageLoadJob.class);
        when(mockJobService.findJobs(JobType.LOADER)).thenReturn(mockJobs);

        http.perform(get(new URI("/api/load"))
            .param("jobType", "LOADER")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(mockJobService).findJobs(JobType.LOADER);
    }

    @Test
    public void testFindAllJobs() throws Exception {
        List mockJobs = randomListOf(3, TestPackageLoadJob.class);
        when(mockJobService.findJobs(null)).thenReturn(mockJobs);

        http.perform(get(new URI("/api/load"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(mockJobService).findJobs(null);
    }

    @Test
    public void shouldStartADeletePackageJob() throws Exception {
        final String testPackageName = "delete-me";

        doNothing().when(mockJobService).startPackageDelete(testPackageName);

        http.perform(delete(String.format("/api/load/%s", testPackageName))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());

        verify(mockJobService).startPackageDelete(testPackageName);
    }
}
