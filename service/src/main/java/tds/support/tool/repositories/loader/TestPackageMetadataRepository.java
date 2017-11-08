package tds.support.tool.repositories.loader;

import org.springframework.data.mongodb.repository.MongoRepository;

import tds.support.tool.model.TestPackageMetadata;

public interface TestPackageMetadataRepository extends MongoRepository<TestPackageMetadata, String> {
}
