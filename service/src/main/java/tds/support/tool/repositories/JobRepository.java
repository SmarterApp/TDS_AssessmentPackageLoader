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
    List<Job> findByUserNameAndTypeIn(final String userName, final JobType... type);

    /**
     * Finds all jobs based on the provided {@link tds.support.job.JobType} (or all jobs if the jobType is null)
     *
     * @param type The type of jobs to fetch
     * @return a collection of jobs corresponding to the specified job type
     */
    List<Job> findByTypeIn(final JobType... type);

    /**
     * Find the most recent {@link tds.support.job.Job} for the specified name and {@link tds.support.job.JobType}.
     *
     * @param name The name of the {@link tds.support.job.Job} to find.  For {@link tds.support.job.JobType#LOAD},
     *             this will be the name of the {@link tds.testpackage.model.TestPackage} file.
     * @param jobType The {@link tds.support.job.JobType} describing the type of {@link tds.support.job.Job} to find
     * @return
     */
    Job findOneByNameAndTypeOrderByCreatedAtDesc(final String name, final JobType jobType);
}
