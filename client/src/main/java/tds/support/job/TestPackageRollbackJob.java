package tds.support.job;

public class TestPackageRollbackJob extends TestPackageDeleteJob {
    private final String parentJobId;

    public TestPackageRollbackJob(final TestPackageLoadJob parentJob) {
        super(parentJob.getName(), parentJob.isSkipArt(), parentJob.isSkipScoring());
        this.setType(JobType.ROLLBACK);
        this.parentJobId = parentJob.getId();
    }

    /**
     * @return The id of the {@link tds.support.job.TestPackageLoadJob} that triggered this rollback job
     */
    public String getParentJobId() {
        return parentJobId;
    }
}
