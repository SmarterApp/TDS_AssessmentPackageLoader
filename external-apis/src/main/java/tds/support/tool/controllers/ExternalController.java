package tds.support.tool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tds.support.job.Job;
import tds.support.tool.services.TestResultsJobService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Created by Greg Charles on 7/30/18.
 */
@RestController
@RequestMapping("api")
public class ExternalController {
    private final TestResultsJobService testResultsJobService;

    @Autowired
    public ExternalController(final TestResultsJobService testResultsJobService) {
        this.testResultsJobService = testResultsJobService;
    }

    /**
     * Gets the scoring jobs
     *
     * @return {@link ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs(final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        return ResponseEntity.ok(testResultsJobService.findJobs(principal.getName()));
    }

    /**
     * Starts the scoring validation job
     *
     * @param fileName name of the TRT being uploaded
     * @param uploadedTRT XML contents of the uploaded TRT
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PostMapping(path="import/{fileName}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadTestResults(
            @PathVariable("fileName") final String fileName, @RequestBody final String uploadedTRT,
                                               final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        final Job job = testResultsJobService.startTestResultsImport(fileName, principal.getName(), uploadedTRT);
        return ResponseEntity.ok(job);
    }

    @PostMapping(path = "scoring/validation/{jobId}",
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleRescoreReport(
            @PathVariable("jobId") final String jobId,
            @RequestBody final String rescoredTrt)
    {
        testResultsJobService.saveRescoredTrt(jobId, rescoredTrt);
        return ResponseEntity.ok("OK");
    }

}
