package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;
import tds.support.tool.services.TestResultsJobService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scoring")
public class ScoringValidationController {
    private final TestResultsJobService testResultsJobService;

    @Autowired
    public ScoringValidationController(final TestResultsJobService testResultsJobService) {
        this.testResultsJobService = testResultsJobService;
    }

    /**
     * Starts the scoring validation job
     *
     * @param file the TRT file to rescore and validate
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadTestResults(@RequestParam("file") final MultipartFile file) throws IOException {
        Job job = testResultsJobService.startTestResultsImport(file.getOriginalFilename(), file.getInputStream(), file.getSize());
        return ResponseEntity.ok(job);
    }

    /**
     * Gets the scoring jobs
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(testResultsJobService.findJobs());
    }

    /**
     * Updates a scoring validation job's status
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PutMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoContentResponseResource> updateJob(@PathVariable final String jobId,
                                                               @RequestBody final JobUpdateRequest request) {
        testResultsJobService.updateJob(jobId, request);
        return ResponseEntity.ok(new NoContentResponseResource());
    }
}
