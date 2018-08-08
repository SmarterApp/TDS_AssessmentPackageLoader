package tds.support.tool.services.loader;

/**
 * A service responsible for placing messages on the messaging queue
 */
public interface MessagingService {
    /**
     * Sends a message to begin the execution of a job's steps
     *
     * @param jobId The job who's steps require execution
     */
    void sendJobStepExecute(final String jobId);
}
