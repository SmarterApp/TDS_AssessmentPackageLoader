package tds.support.tool.repositories.loader;

import org.springframework.data.mongodb.repository.MongoRepository;
import tds.testpackage.model.TestPackage;

public interface MongoTestPackageRepository extends MongoRepository<TestPackage, String> {
}
