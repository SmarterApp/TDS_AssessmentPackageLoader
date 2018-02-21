package tds.support.job;

public class TestPackageRollbackJob extends TestPackageDeleteJob {
    private final String parentJobId;

    public TestPackageRollbackJob(final String parentJobId, final String name, final boolean skipArt, final boolean skipScoring) {
        super(name, skipArt, skipScoring);
        this.setType(JobType.ROLLBACK);
        this.parentJobId = parentJobId;
    }

    /**
     * @return The id of the {@link tds.support.job.TestPackageLoadJob} that triggered this rollback job
     */
    public String getParentJobId() {
        return parentJobId;
    }
}
