package tds.support.tool.services;

import java.util.List;

import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageStatus;

/**
 * Manage/interact with {@link tds.support.job.TestPackageStatus}es.
 */
public interface TestPackageStatusService {
    /**
     * Create a new {@link tds.support.job.TestPackageStatus} to record the state of the test package that is being
     * loaded by the {@link tds.support.job.TestPackageLoadJob}.
     *
     * @param job The {@link tds.support.job.TestPackageLoadJob} that is loading the test package
     * @return The {@link tds.support.job.TestPackageStatus} that was created
     */
    TestPackageStatus create(final TestPackageLoadJob job);

    /**
     * Update a {@link tds.support.job.TestPackageStatus} record with new data
     *
     * @param testPackageStatus The {@link tds.support.job.TestPackageStatus} to update
     * @return The updated {@link tds.support.job.TestPackageStatus}
     */
    TestPackageStatus update(final TestPackageStatus testPackageStatus);

    /**
     * Fetch all {@link tds.support.job.TestPackageStatus}es
     *
     * @return A collection of all the {@link tds.support.job.TestPackageStatus} records
     */
    List<TestPackageStatus> getAll();
}
