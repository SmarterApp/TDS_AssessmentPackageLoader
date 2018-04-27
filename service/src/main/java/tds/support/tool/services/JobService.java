package tds.support.tool.services;

import org.springframework.security.access.prepost.PreAuthorize;
import tds.support.job.Job;
import tds.support.job.JobType;

import java.io.InputStream;
import java.util.List;

/**
 * Service handling the {@link tds.support.job.Job}
 */
public interface JobService {
    /**
     * Starts the test package import job resulting in a job loaded into TDS, TIS, THSS, and ART
     *
     * @param packageName     the test package name
     * @param testPackage     the {@link java.io.InputStream} containing the test package
     * @param testPackageSize the size of the test package
     * @param skipArt         {@code true} if ART is to be skipped in the upload
     * @param skipScoring     {@code true} if scoring is to be skipped in the upload
     * @return the {@link tds.support.job.Job} created
     */

    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    Job startPackageImport(final String packageName, final InputStream testPackage, long testPackageSize,
                           final boolean skipArt, final boolean skipScoring);

    /**
     * Finds all jobs matching a specific {@link tds.support.job.JobType}
     *
     * @param jobTypes The type of jobs to fetch
     * @return A collection of all jobs matching
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    List<Job> findJobs(final JobType... jobTypes);

    /**
     * Executes a job step for the specified job ID
     *
     * @param jobId The id of the job to execute
     */
    void executeJobSteps(final String jobId);

    /**
     * Create a {@link tds.support.job.TestPackageDeleteJob} that will remove the
     * {@link tds.testpackage.model.TestPackage} from all the systems it's loaded into.
     *
     * @param testPackageName The name of the {@link tds.testpackage.model.TestPackage}.
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    void startPackageDelete(final String testPackageName);
}
