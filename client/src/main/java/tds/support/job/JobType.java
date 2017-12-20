package tds.support.job;

/**
 * The types of jobs supported by the application
 */
public enum JobType {
    /**
     * Job that rescores an existing TRT
     */
    SCORING,

    /**
     * Loads a test package into the TDS system including supporting services
     */
    LOADER,

    /**
     * Deletes a test package from the TDS system and supporting services
     */
    DELETE,

    /**
     * Functionally the same as a DELETE job, but is triggered implicitly by a failed LOADER job
     */
    ROLLBACK
}
