package tds.support.tool.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    TestPackageStatus save(final Job job);

    /**
     * Fetch a page of {@link tds.support.job.TestPackageStatus}es
     *
     * @param pageable The {@link org.springframework.data.domain.Pageable} containing the paging information for this
     *                 request
     * @return A {@link org.springframework.data.domain.Page} of {@link tds.support.job.TestPackageStatus} records
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    Page<TestPackageStatus> getAll(final Pageable pageable);

    /**
     * Find all {@link tds.support.job.TestPackageStatus}es that match the provided test package name
     *
     * @param testPackageNameFragment The part of the test package name to search by
     * @param pageable The {@link org.springframework.data.domain.Pageable} containing the paging information for this
     *                 request
     * @return A {@link org.springframework.data.domain.Page} of {@link tds.support.job.TestPackageStatus}es that have a
     * name like the provided test package name fragment.
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    Page<TestPackageStatus> searchByName(final String testPackageNameFragment, final Pageable pageable);

    /**
     * Delete a {@link tds.support.job.TestPackageStatus} record for a {@link tds.testpackage.model.TestPackage} that is
     * no longer in the system.
     *
     * @param testPackageName The name of the {@link tds.testpackage.model.TestPackage} to delete
     */
    @PreAuthorize("hasAuthority('PERM_SUPPORT_TOOL_ADMINISTRATION')")
    void delete(final String testPackageName);
}