package tds.support.tool.controllers;

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
import tds.support.job.Job;
import tds.support.job.ScoringValidationReport;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.services.TestResultsJobService;
import tds.trt.model.TDSReport;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExternalController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class ExternalControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @MockBean
    private TestResultsJobService mockTestResultsJobService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldFindScoringJobs() throws Exception {
        List mockJobs = randomListOf(3, TestResultsScoringJob.class);
        when(mockTestResultsJobService.findJobs("username")).thenReturn(mockJobs);

        http.perform(get(new URI("/api/scoring"))
                .with(user("username"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findJobs("username");
    }

    @Test
    public void shouldLoadTestResults() throws Exception {
        final String fileName = "sample_TRT";
        final String username = "username";
        final String content = "<TDSReport />";

        http.perform(post(new URI("/api/scoring/import/" + fileName))
                .with(user(username))
                .contentType(MediaType.APPLICATION_XML)
                .content(content.getBytes()))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).startTestResultsImport(fileName, username, content);
     }

    @Test
    public void shouldGetRescoredTrt() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", username);
        TDSReport rescoredTrt = new TDSReport();
        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setClientName("SBAC");
        opportunity.setKey("A key");
        rescoredTrt.setOpportunity(opportunity);

        when(mockTestResultsJobService.findRescoredTrt("jobId")).thenReturn(Optional.of(rescoredTrt));
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/rescored"))
                .with(user(username))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findRescoredTrt("jobId");
        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGet404ForNoRescoredTrtFound() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", username);
        when(mockTestResultsJobService.findRescoredTrt("jobId")).thenReturn(Optional.empty());
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/rescored"))
                .with(user(username))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findRescoredTrt("jobId");
        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGet401ForDifferentUserRescoredTrt() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", "another username");
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/rescored"))
                .with(user(username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGetScoringValidationReport() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", username);
        when(mockTestResultsJobService.findScoringValidationReport("jobId"))
                .thenReturn(Optional.of(new ScoringValidationReport("jobId",null)));
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/report"))
                .with(user(username))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findScoringValidationReport("jobId");
    }

    @Test
    public void shouldGet404ForNoScoringValidationReportFound() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", username);
        when(mockTestResultsJobService.findScoringValidationReport("jobId")).thenReturn(Optional.empty());
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/report"))
                .with(user(username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findScoringValidationReport("jobId");
        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGet401ForDifferentUserReport() throws Exception {
        final String username = "user1";
        Job mockJob = new TestResultsScoringJob("name", "another username");
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/report"))
                .with(user(username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(mockTestResultsJobService).findJob("jobId");
    }
}