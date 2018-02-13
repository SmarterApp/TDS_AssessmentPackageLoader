package tds.support.tool.repositories.loader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import tds.support.job.TestPackageStatus;

/**
 * Interacts with Test Package status information.
 */
public interface TestPackageStatusRepository extends MongoRepository<TestPackageStatus, String> {
    TestPackageStatus findByName(final String testPackageName);

    Page<TestPackageStatus> findAllByNameContainingIgnoreCase(final String searchTerm, final Pageable pageable);
}