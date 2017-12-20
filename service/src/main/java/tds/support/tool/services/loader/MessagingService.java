package tds.support.tool.services.loader;

public interface MessagingService {
    /**
     * Sends a message to begin the execution of a job step
     *
     * @param jobId The job who's steps require execution
     */
    void sendJobStepExecute(final String jobId);
}
