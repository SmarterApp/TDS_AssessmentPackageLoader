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
    LOADER
}
