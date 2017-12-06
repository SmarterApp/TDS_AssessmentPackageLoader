package tds.support.tool.model;

public class JobInfo {
    private String jobId;
    private String nextStepName;

    //For frameworks
    JobInfo() { }

    public JobInfo(final String jobId, final String nextStepName) {
        this.jobId = jobId;
        this.nextStepName = nextStepName;
    }

    public String getJobId() {
        return jobId;
    }

    public String getNextStepName() {
        return nextStepName;
    }
}
