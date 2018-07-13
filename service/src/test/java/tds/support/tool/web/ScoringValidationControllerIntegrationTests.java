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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.JobUpdateRequest;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.services.TestResultsJobService;

import java.net.URI;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ScoringValidationController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class ScoringValidationControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @MockBean
    private TestResultsJobService mockTestResultsJobService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldFindScoringJobs() throws Exception {
        List mockJobs = randomListOf(3, TestResultsScoringJob.class);
        when(mockTestResultsJobService.findJobs()).thenReturn(mockJobs);

        http.perform(get(new URI("/api/scoring"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findJobs();
    }

    @Test
    public void shouldUpdateScoringJob() throws Exception {
        JobUpdateRequest request = random(JobUpdateRequest.class);

        http.perform(MockMvcRequestBuilders.put(new URI("/api/scoring/jobId"))
                .content(objectMapper.writer().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).updateJob(eq("jobId"), isA(JobUpdateRequest.class));
    }

}
