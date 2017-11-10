package tds.support.job;

/**
 * Contains error information for steps within a job
 */
public class Error {
    private String message;
    private ErrorSeverity severity;

    Error(){}

    public Error(final String message, final ErrorSeverity severity) {
        this.message = message;
        this.severity = severity;
    }

    /**
     * @return the error message containing why the error occurred
     */
    public String getMessage() {
        return message;
    }

    void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return the {@link tds.support.job.ErrorSeverity} of the error.
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }

    void setSeverity(final ErrorSeverity severity) {
        this.severity = severity;
    }
}
