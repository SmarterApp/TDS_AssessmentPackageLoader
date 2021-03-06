package tds.support.tool.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import tds.support.job.Job;
import tds.support.job.TestPackageStatus;

/**
 * Manage/interact with {@link tds.support.job.TestPackageStatus}es.
 */
public interface TestPackageStatusService {
    /**
     * Create a new {@link tds.support.job.TestPackageStatus} to record the state of the test package that is being
     * loaded by the {@link tds.support.job.Job}.
     *
     * @param job The {@link tds.support.job.Job} that is loading the test package
     * @return The {@link tds.support.job.TestPackageStatus} that was created
     */
    TestPackageStatus save(final Job job);

    /**
     * Fetch a list of {@link tds.support.job.TestPackageStatus}es
     *
     * @return A collection of all statuses
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION') or hasAuthority('PERM_TEST_PACKAGE_LOADER')")
    List<TestPackageStatus> getAll();

    /**
     * Delete a {@link tds.support.job.TestPackageStatus} record for a {@link tds.testpackage.model.TestPackage} that is
     * no longer in the system.
     *
     * @param testPackageName The name of the {@link tds.testpackage.model.TestPackage} to delete
     */
    void delete(final String testPackageName);
}