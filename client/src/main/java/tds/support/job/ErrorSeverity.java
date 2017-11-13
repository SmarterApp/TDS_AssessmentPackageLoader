package tds.support.job;

/**
 * Contains the severity of the errors.
 */
public enum ErrorSeverity {
    /**
     * Represents a critical error that will stop the job
     */
    CRITICAL,

    /**
     * Represents a warning with one of the steps in the job but may not stop the job from processing
     */
    WARN
}
