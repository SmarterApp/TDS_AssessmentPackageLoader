package tds.support.tool.repositories.loader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import tds.support.job.TestPackageStatus;

/**
 * Interacts with Test Package status information.
 */
public interface TestPackageStatusRepository extends MongoRepository<TestPackageStatus, String> {
    /**
     * Find a single {@link tds.support.job.TestPackageStatus} by its name
     *
     * @param testPackageName The name of the {@link tds.support.job.TestPackageStatus} to search for
     * @return A {@link tds.support.job.TestPackageStatus} for the specified name
     */
    TestPackageStatus findByName(final String testPackageName);

    /**
     * Conduct a case-insensitive search for a collection of {@link tds.support.job.TestPackageStatus}es by name.
     *
     * @param searchTerm The test package name fragment to search for
     * @param pageable The {@link org.springframework.data.domain.Pageable} describing the paging information
     * @return A {@link Page<tds.support.job.TestPackageStatus>} representing the a page of results that match the
     * search term.
     */
    Page<TestPackageStatus> findAllByNameContainingIgnoreCase(final String searchTerm, final Pageable pageable);
}