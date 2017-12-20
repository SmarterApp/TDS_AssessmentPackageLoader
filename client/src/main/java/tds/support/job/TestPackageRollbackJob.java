package tds.support.job;

public class TestPackageRollbackJob extends TestPackageDeleteJob {
    private final String parentJobId;

    public TestPackageRollbackJob(final String parentJobId, final String testPackageFileName, final boolean skipArt, final boolean skipScoring) {
        super(testPackageFileName, skipArt, skipScoring);
        this.setType(JobType.ROLLBACK);
        this.parentJobId = parentJobId;
    }

    public String getParentJobId() {
        return parentJobId;
    }
}
