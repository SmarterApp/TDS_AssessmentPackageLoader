package tds.support.tool.repositories.scoring;

import org.springframework.data.mongodb.repository.MongoRepository;
import tds.support.job.TestResultsWrapper;

public interface MongoTestResultsRepository extends MongoRepository<TestResultsWrapper, String> {
}
