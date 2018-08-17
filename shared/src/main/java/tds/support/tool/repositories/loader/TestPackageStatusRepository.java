package tds.support.tool.repositories.loader;

import org.springframework.data.mongodb.repository.MongoRepository;

import tds.support.job.TestPackageStatus;

/**
 * Interacts with Test Package status information.
 */
public interface TestPackageStatusRepository extends MongoRepository<TestPackageStatus, String> {
}