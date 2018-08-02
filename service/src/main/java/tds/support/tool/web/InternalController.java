package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;
import tds.support.tool.services.TestResultsJobService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Endpoints that will be consumed by other TDS modules. They will be unsecured, but served on
 * a different port, which will be firewalled off from public access.
 */
@RestController
@RequestMapping(value = "/internal")
public class InternalController {
    private final TestResultsJobService testResultsJobService;

    @Autowired
    public InternalController(final TestResultsJobService testResultsJobService) {
        this.testResultsJobService = testResultsJobService;
    }

    /**
     * Gets the scoring jobs
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs(final HttpServletRequest request) {
        return ResponseEntity.ok(testResultsJobService.findJobs("ca.admin@example.com"));
    }

    /**
     * Handles and stores the re-scored TRT for the given job id.
     * @param jobId the job id for the original TRT
     * @param rescoredTrt the re-scored TRT for storage
     * @return {@link org.springframework.http.ResponseEntity} containing the job ID}
     */
    @PostMapping(path = "scoring/validation/{jobId}",
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleRescoreReport(
            @PathVariable("jobId") final String jobId,
            @RequestBody final String rescoredTrt)
    {
        testResultsJobService.saveRescoredTrt(jobId, rescoredTrt);
        return ResponseEntity.ok("OK");
    }

    /**
     * Updates a scoring validation job's status
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PutMapping(path = "scoring/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoContentResponseResource> updateJob(@PathVariable final String jobId,
                                                               @RequestBody final JobUpdateRequest request) {
        testResultsJobService.updateJob(jobId, request);
        return ResponseEntity.ok(new NoContentResponseResource());
    }

}
