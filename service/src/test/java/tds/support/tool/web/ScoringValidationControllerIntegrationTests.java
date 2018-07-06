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
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.services.TestResultsJobService;

import java.net.URI;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
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

    @Test
    public void shouldFindScoringJobs() throws Exception {
        List mockJobs = randomListOf(3, TestResultsScoringJob.class);
        when(mockTestResultsJobService.findJobs()).thenReturn(mockJobs);

        http.perform(get(new URI("/api/scoring"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findJobs();
    }

}
