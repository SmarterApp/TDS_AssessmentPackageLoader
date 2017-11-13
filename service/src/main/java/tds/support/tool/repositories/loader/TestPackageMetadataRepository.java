package tds.support.tool.repositories.loader;

import org.springframework.data.mongodb.repository.MongoRepository;

import tds.support.tool.model.TestPackageMetadata;

/**
 * Handles storing the {@link tds.support.tool.model.TestPackageMetadata} in MongoDB
 */
public interface TestPackageMetadataRepository extends MongoRepository<TestPackageMetadata, String> {
}
