package tds.support.job;

public class JobUpdateRequest {
    private String name;
    private TargetSystem targetSystem;
    private Status status;
    private String description;

    private JobUpdateRequest() {
    }

    public JobUpdateRequest(final String name, final TargetSystem targetSystem, final Status status, final String description) {
        this.name = name;
        this.targetSystem = targetSystem;
        this.status = status;
        this.description = description;
    }

    public TargetSystem getTargetSystem() {
        return targetSystem;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
