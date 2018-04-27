package tds.support.tool.validation;

import tds.support.job.ErrorSeverity;

public class ValidationError {
    private ErrorSeverity severity;
    private String message;

    public ValidationError(final ErrorSeverity severity, final String message) {
        this.severity = severity;
        this.message = message;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
