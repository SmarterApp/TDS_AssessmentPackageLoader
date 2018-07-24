package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.Principal;
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
    public ResponseEntity<Job> loadTestResults(@RequestParam("file") final MultipartFile file,
                                               final HttpServletRequest request) throws IOException {
        final Principal principal = request.getUserPrincipal();
        final Job job = testResultsJobService.startTestResultsImport(file.getOriginalFilename(), principal.getName(), file.getInputStream(), file.getSize());
        return ResponseEntity.ok(job);
    }

    // TODO: this may need to move to a different controller to allow it to be accessible to non-browser clients
    /**
     * Handles and stores the re-scored TRT for the given job id.
     * @param jobId the job id for the original TRT
     * @param rescoredTrt the re-scored TRT for storage
     * @return {@link org.springframework.http.ResponseEntity} containing the job ID}
     */
    @PostMapping(path = "validation/{jobId}",
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleRescoreReport(
            @PathVariable("jobId") final String jobId,
            @RequestBody final String rescoredTrt)
    {
        testResultsJobService.saveRescoredTrt(jobId, rescoredTrt);
        return ResponseEntity.ok("OK");
    }

    /**
     * Gets the scoring jobs
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs(final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        return ResponseEntity.ok(testResultsJobService.findJobs(principal.getName()));
    }

    /**
     * Gets a specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> getJob(final HttpServletRequest request, @PathVariable final String jobId) {
        final Principal principal = request.getUserPrincipal();
        final Optional<Job> maybeJob = testResultsJobService.findJob(jobId);

        if (!maybeJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // If the job does not belong to the user, return a 401
        if (!maybeJob.get().getUserName().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(maybeJob.get());
    }


    /**
     * Gets an original TRT uploaded with the specified job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the original stringified {@link TDSReport}
     */
    @GetMapping(value = "/{jobId}/original", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getOriginalTrt(final HttpServletRequest request, @PathVariable final String jobId) throws JAXBException {
        final Principal principal = request.getUserPrincipal();
        final Optional<Job> maybeJob = testResultsJobService.findJob(jobId);

        if (!maybeJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // If the job does not belong to the user, return a 401
        if (!maybeJob.get().getUserName().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final Optional<TDSReport> maybeTrt = testResultsJobService.findOriginalTrt(jobId);

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
    public ResponseEntity<String> getRescoredTrt(final HttpServletRequest request, @PathVariable final String jobId) throws JAXBException {
        final Principal principal = request.getUserPrincipal();
        final Optional<Job> maybeJob = testResultsJobService.findJob(jobId);

        if (!maybeJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // If the job does not belong to the user, return a 401
        if (!maybeJob.get().getUserName().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final Optional<TDSReport> maybeTrt = testResultsJobService.findRescoredTrt(jobId);

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
    public ResponseEntity<ScoringValidationReport> getScoringValidationReport(final HttpServletRequest request,
                                                                              @PathVariable final String jobId) {
        final Principal principal = request.getUserPrincipal();
        final Optional<Job> maybeJob = testResultsJobService.findJob(jobId);

        if (!maybeJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // If the job does not belong to the user, return a 401
        if (!maybeJob.get().getUserName().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final Optional<ScoringValidationReport> maybeReport = testResultsJobService.findScoringValidationReport(jobId);

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
