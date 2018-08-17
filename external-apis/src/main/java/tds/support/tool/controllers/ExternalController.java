package tds.support.tool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tds.support.job.Job;
import tds.support.job.ScoringValidationReport;
import tds.support.tool.services.TestResultsJobService;
import tds.support.tool.services.impl.TestResultsMarshaller;
import tds.trt.model.TDSReport;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
     */
    @GetMapping(path="scoring", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs(final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        return ResponseEntity.ok(testResultsJobService.findJobs(principal.getName()));
    }

    /**
     * Gets a specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     */
    @GetMapping(value = "scoring/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Starts the scoring validation job
     *
     * @param fileName name of the TRT being uploaded
     * @param uploadedTRT XML contents of the uploaded TRT
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     */
    @PostMapping(path="scoring/import/{fileName}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadTestResults(
            @PathVariable("fileName") final String fileName, @RequestBody final String uploadedTRT,
                                               final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        final Job job = testResultsJobService.startTestResultsImport(fileName, principal.getName(), uploadedTRT);
        return ResponseEntity.ok(job);
    }

    /**
     * Gets rescored TRT for give job Id specific scoring job
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the rescored stringified {@link TDSReport}
     */
    @GetMapping(value = "scoring/{jobId}/rescored", produces = MediaType.APPLICATION_XML_VALUE)
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
     * Gets a scoring difference report for the give job id.
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the rescored stringified {@link TDSReport}
     */
    @GetMapping(value = "scoring/{jobId}/report", produces = MediaType.APPLICATION_JSON_VALUE)
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

}
