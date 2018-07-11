package tds.support.tool.services;

import org.springframework.security.access.prepost.PreAuthorize;
import tds.support.job.Job;
import tds.support.job.JobUpdateRequest;

import java.io.InputStream;
import java.util.List;

public interface TestResultsJobService {

    /**
     * Starts the scoring/TRT import job resulting in a job loaded into TDS, TIS, THSS, and ART
     *
     * @param packageName     the test package name
     * @param testPackage     the {@link java.io.InputStream} containing the test package
     * @param testPackageSize the size of the test package
     * @return the {@link tds.support.job.Job} created
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    Job startTestResultsImport(final String packageName, final InputStream testPackage, final long testPackageSize);

    /**
     * Executes all registered job steps for the specified job
     *
     * @param jobId The id of the job to execute
     */
    void executeJobSteps(final String jobId);

    /**
     * Returns all scoring validation jobs
     * @return A list of all scoring validation {@link Job}s
     */
    List<Job> findJobs();

    void updateJob(final String jobId, final JobUpdateRequest request);
}
