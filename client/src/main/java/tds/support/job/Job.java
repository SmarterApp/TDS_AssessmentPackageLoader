package tds.support.job;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Job {
    @Id
    private String id;
    private JobType type;
    private Status status;
    private List<Step> steps = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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
}
