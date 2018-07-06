package tds.support.tool.repositories.scoring;

import org.springframework.data.mongodb.repository.MongoRepository;
import tds.support.tool.model.TestResultsMetadata;

/**
 * Handles storing the {@link TestResultsMetadata} in MongoDB
 */
public interface TestResultsMetadataRepository extends MongoRepository<TestResultsMetadata, String> {
    TestResultsMetadata findByJobId(final String jobId);
}
