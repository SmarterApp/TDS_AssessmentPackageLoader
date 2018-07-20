package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;
import tds.support.job.ScoringValidationReport;
import tds.support.tool.services.TestResultsJobService;
import tds.support.tool.services.impl.TestResultsMarshaller;
import tds.trt.model.TDSReport;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
     * Gets a specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> getJob(@PathVariable final String jobId) {
        Optional<Job> maybeJob = testResultsJobService.findJob(jobId);

        if (!maybeJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(maybeJob.get());
    }


    /**
     * Gets an original TRT uploaded with the specified job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the original stringified {@link TDSReport}
     */
    @GetMapping(value = "/{jobId}/original", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getOriginalTrt(@PathVariable final String jobId) throws JAXBException {
        Optional<TDSReport> maybeTrt = testResultsJobService.findOriginalTrt(jobId);

        if (!maybeTrt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(TestResultsMarshaller.mashall(maybeTrt.get()));
    }

    /**
     * Gets a specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the rescored stringified {@link TDSReport}
     */
    @GetMapping(value = "/{jobId}/rescored", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRescoredTrt(@PathVariable final String jobId) throws JAXBException {
        Optional<TDSReport> maybeTrt = testResultsJobService.findRescoredTrt(jobId);

        if (!maybeTrt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(TestResultsMarshaller.mashall(maybeTrt.get()));
    }

    /**
     * Gets a specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the rescored stringified {@link TDSReport}
     */
    @GetMapping(value = "/{jobId}/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScoringValidationReport> getScoringValidationReport(@PathVariable final String jobId) {
        Optional<ScoringValidationReport> maybeReport = testResultsJobService.findScoringValidationReport(jobId);

        if (!maybeReport.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(maybeReport.get());
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
