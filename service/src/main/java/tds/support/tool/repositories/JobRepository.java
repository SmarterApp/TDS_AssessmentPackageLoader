package tds.support.tool.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import tds.support.job.Job;
import tds.support.job.JobType;

/**
 * Handles MongoDB operations for {@link tds.support.job.Job}
 */
public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByType(JobType type);
}
