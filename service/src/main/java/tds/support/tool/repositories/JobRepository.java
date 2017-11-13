package tds.support.tool.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import tds.support.job.Job;

/**
 * Handles MongoDB operations for {@link tds.support.job.Job}
 */
public interface JobRepository extends MongoRepository<Job, String> {
}
