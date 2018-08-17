package tds.support.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a step in a {@link tds.support.job.Job}
 */
public class Step {
    private static final Logger log = LoggerFactory.getLogger(Step.class);
    private String description;
    private List<Error> errors = new ArrayList<>();
    private Status status;
    private String name;
    private TargetSystem target;
    private boolean complete;
    private LocalDateTime createdAt;

    /**
     * For frameworks/serialization
     */
    private Step() {

    }

    public Step(final String name, final TargetSystem target, final String description) {
        this(name, target, description, Status.NOT_STARTED);
    }

    public Step(final String name, final TargetSystem target, final String description, final Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.target = target;
        this.createdAt = LocalDateTime.now();
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

    void setErrors(final List<Error> errors) {
        this.errors = errors;
    }

    /**
     * @return an unmodifiable list of errors. {@link tds.support.job.Error} associated with the object
     */
    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * @return the step name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return The system being targeted by the job step
     */
    public TargetSystem getJobStepTarget() {
        return this.target;
    }

    public void setComplete(final boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public static void handleException(final Job job, final Step step, final Exception e) {
        log.error("An error occurred for step {} with job ID {}", step.getName(), job.getId(), e);

        step.setStatus(Status.FAIL);
        step.addError(new Error(String.format("Error occurred while communicating with %s: %s", step.target, e.getMessage()),
                ErrorSeverity.CRITICAL));
    }

    /**
     * @return The time and date that the {@link tds.support.job.Job} was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
