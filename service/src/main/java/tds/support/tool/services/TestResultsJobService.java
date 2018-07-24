package tds.support.tool.services;

import org.springframework.security.access.prepost.PreAuthorize;
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;
import tds.support.job.ScoringValidationReport;
import tds.trt.model.TDSReport;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface TestResultsJobService {

    /**
     * Starts the scoring/TRT import job resulting in a job loaded into TDS, TIS, THSS, and ART
     *
     * @param packageName     the test package name
     * @param username        the name of the user who uploaded the test results and initiated the scoring job
     * @param testPackage     the {@link java.io.InputStream} containing the test package
     * @param testPackageSize the size of the test package
     * @return the {@link tds.support.job.Job} created
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    Job startTestResultsImport(final String packageName, final String username, final InputStream testPackage, final long testPackageSize);

    /**
     * Executes all registered job steps for the specified job
     *
     * @param jobId The id of the job to execute
     */
    void executeJobSteps(final String jobId);

    /**
     * Returns all scoring validation jobs
     *
     * @param username The username of the user who's jobs to fetch
     * @return A list of all scoring validation {@link Job}s
     */
    List<Job> findJobs(final String username);

    /**
     * Updates the status of a test package job
     *
     * @param jobId   The id of the job to update
     * @param request an object with status information
     */
    void updateJob(final String jobId, final JobUpdateRequest request);

    /**
     * Returns a specific scoring validation job
     *
     * @param jobId The id of the job to fetch
     * @return The job with the specified jobId, if it exists
     */
    Optional<Job> findJob(final String jobId);

    /**
     * Finds the original TRT uploaded for the specified job
     *
     * @param jobId The job id of the TRT
     * @return The TRT
     */
    Optional<TDSReport> findOriginalTrt(final String jobId);

    /**
     * Finds the rescored TRT uploaded for the specified job
     *
     * @param jobId The job id of the TRT
     * @return The TRT
     */
    Optional<TDSReport> findRescoredTrt(final String jobId);

    /**
     * Finds the scoring validation report for the specified job
     *
     * @param jobId The id of the job the report was generated for
     * @return The scoring validation report
     */
    Optional<ScoringValidationReport> findScoringValidationReport(String jobId);

    /**
     * Saves the score validation report (result of the re-score process)
     * @param jobId the id of the test results scoring job
     * @param rescoredTrtString the re-scored TRT as an XML string
     */
    void saveRescoredTrt(String jobId, String rescoredTrtString);
}
