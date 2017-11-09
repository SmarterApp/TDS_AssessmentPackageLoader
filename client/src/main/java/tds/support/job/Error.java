package tds.support.job;

public class Error {
    private String message;
    private ErrorSeverity severity;

    public Error(){}

    public Error(final String message, final ErrorSeverity severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(final ErrorSeverity severity) {
        this.severity = severity;
    }
}
