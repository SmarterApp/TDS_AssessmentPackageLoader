package tds.support.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Job {
    @Id
    private String id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt = LocalDateTime.now();
    private JobType type;
    private Status status;
    private List<Step> steps = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public JobType getType() {
        return type;
    }

    public void setType(final JobType type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(final List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(final Step step) {
        steps.add(step);
    }

    public Optional<Step> getStepByName(final String stepName) {
        return steps.stream().filter(step -> stepName.equals(step.getName())).findFirst();
    }
}
