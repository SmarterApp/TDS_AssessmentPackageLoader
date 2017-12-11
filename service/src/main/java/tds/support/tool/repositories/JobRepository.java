package tds.support.tool.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import tds.support.job.Job;
import tds.support.job.JobType;

/**
 * Handles MongoDB operations for {@link tds.support.job.Job}
 */
public interface JobRepository extends MongoRepository<Job, String> {
    /**
     * Finds all jobs based on the provided {@link tds.support.job.JobType} (or all jobs if the jobType is null)
     *
     * @param type The type of jobs to fetch
     * @return a collection of jobs corresponding to the specified job type
     */
    List<Job> findByType(JobType type);
}
