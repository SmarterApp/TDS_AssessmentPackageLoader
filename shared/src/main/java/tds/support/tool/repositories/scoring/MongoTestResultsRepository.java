package tds.support.tool.repositories.scoring;

import org.springframework.data.mongodb.repository.MongoRepository;
import tds.support.job.TestResultsWrapper;

public interface MongoTestResultsRepository extends MongoRepository<TestResultsWrapper, String> {
    /**
     * Finds an original TRT based on the job id
     * @param jobId The jobId of original TRT
     * @return The original TRT {@link tds.trt.model.TDSReport} wrapped object
     */
    TestResultsWrapper findByJobId(final String jobId);
}
