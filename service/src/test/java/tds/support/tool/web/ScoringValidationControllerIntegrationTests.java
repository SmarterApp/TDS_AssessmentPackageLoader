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
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;
import tds.support.job.ScoringValidationReport;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.services.TestResultsJobService;
import tds.trt.model.TDSReport;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

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

    @Test
    public void shouldGetScoringJob() throws Exception {
        Job mockJob = new TestResultsScoringJob("name");
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.of(mockJob));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(mockJob.getName())));

        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGet404ForNoJob() throws Exception {
        when(mockTestResultsJobService.findJob("jobId")).thenReturn(Optional.empty());

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findJob("jobId");
    }

    @Test
    public void shouldGetOriginalTrt() throws Exception {
        TDSReport originalTrt = new TDSReport();
        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setClientName("SBAC");
        opportunity.setKey("A key");
        originalTrt.setOpportunity(opportunity);
        when(mockTestResultsJobService.findOriginalTrt("jobId")).thenReturn(Optional.of(originalTrt));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/original"))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/TDSReport/Opportunity/@clientName").string("SBAC"))
                .andExpect(xpath("/TDSReport/Opportunity/@key").string("A key"));

        verify(mockTestResultsJobService).findOriginalTrt("jobId");
    }

    @Test
    public void shouldGet404ForNoOriginalTrtFound() throws Exception {
        when(mockTestResultsJobService.findOriginalTrt("jobId")).thenReturn(Optional.empty());

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/original"))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findOriginalTrt("jobId");
    }

    @Test
    public void shouldGetRescoredTrt() throws Exception {
        TDSReport rescoredTrt = new TDSReport();
        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setClientName("SBAC");
        opportunity.setKey("A key");
        rescoredTrt.setOpportunity(opportunity);
        when(mockTestResultsJobService.findRescoredTrt("jobId")).thenReturn(Optional.of(rescoredTrt));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/rescored"))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/TDSReport/Opportunity/@clientName").string("SBAC"))
                .andExpect(xpath("/TDSReport/Opportunity/@key").string("A key"));

        verify(mockTestResultsJobService).findRescoredTrt("jobId");
    }

    @Test
    public void shouldGet404ForNoRescoredTrtFound() throws Exception {
        when(mockTestResultsJobService.findRescoredTrt("jobId")).thenReturn(Optional.empty());

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/rescored"))
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findRescoredTrt("jobId");
    }

    @Test
    public void shouldGetScoringValidationReport() throws Exception {
        when(mockTestResultsJobService.findScoringValidationReport("jobId"))
                .thenReturn(Optional.of(new ScoringValidationReport()));

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/report"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockTestResultsJobService).findScoringValidationReport("jobId");
    }

    @Test
    public void shouldGet404ForNoScoringValidationReportFound() throws Exception {
        when(mockTestResultsJobService.findScoringValidationReport("jobId")).thenReturn(Optional.empty());

        http.perform(MockMvcRequestBuilders.get(new URI("/api/scoring/jobId/report"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(mockTestResultsJobService).findScoringValidationReport("jobId");
    }
}
