package tds.support.job;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a step in a {@link tds.support.job.Job}
 */
public class Step {
    private String description;
    private List<Error> errors;
    private Status status;

    public Step() {
    }

    public Step(String description, Status status) {
        this.description = description;
        this.status = status;
    }

    /**
     * @return description for the step in the job
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setErrors(final List<Error> errors) {
        this.errors = errors;
    }

    /**
     * @return the {@link tds.support.job.Status} for the step
     */
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * Adds an error to the list of errors in the step.  There could be many especially if they are not critical failures
     *
     * @param error the {@link tds.support.job.Error}
     */
    public void addError(final Error error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }

        errors.add(error);
    }
}
