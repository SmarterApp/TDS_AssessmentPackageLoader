package tds.support.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Job {
    @Id
    private String id;
    private String name;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt = LocalDateTime.now();
    private JobType type;
    private List<Step> steps = new ArrayList<>();
    private String userName;

    /**
     * @return The identifier of the {@link tds.support.job.Job}
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return The name of the {@link tds.support.job.Job}
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    /**
     * @return The type of the {@link tds.support.job.Job}
     */
    public JobType getType() {
        return type;
    }

    public void setType(final JobType type) {
        this.type = type;
    }

    /**
     * @return The current status of the {@link tds.support.job.Job}
     */
    public abstract Status getStatus();

    public List<Step> getSteps() {
        return steps;
    }

    /**
     * @param steps The steps (sequential) of the {@link tds.support.job.Job}
     */
    public void setSteps(final List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(final Step step) {
        steps.add(step);
    }

    /**
     * A helper method for fetching a {@link tds.support.job.Step} in the job based on its name
     *
     * @param stepName The name of the step to fetch
     * @return The step with the matching name
     */
    public Optional<Step> getStepByName(final String stepName) {
        return steps.stream().filter(step -> stepName.equals(step.getName())).findFirst();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }
}
