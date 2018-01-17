package tds.support.job;

import java.util.ArrayList;
import java.util.List;

public class TestPackageLoadJob extends Job {
    private final String testPackageFileName;
    private static final String TEST_PACKAGE_PREFIX = "test-package";
    public static final String FILE_UPLOAD = TEST_PACKAGE_PREFIX + "-file-upload";
    public static final String VALIDATE = TEST_PACKAGE_PREFIX + "-validate";
    public static final String ART_UPLOAD = TEST_PACKAGE_PREFIX + "-art-upload";
    public static final String TDS_UPLOAD = TEST_PACKAGE_PREFIX + "-tds-upload";
    public static final String TIS_UPLOAD = TEST_PACKAGE_PREFIX + "-tis-upload";
    public static final String THSS_UPLOAD = TEST_PACKAGE_PREFIX + "-thss-upload";
    // These properties are not set explicitly in code - these properties are automatically populated by
    // Spring Data and serialized before being sent to the client.
    private final boolean skipArt;
    private final boolean skipScoring;

    public TestPackageLoadJob(final String testPackageFileName, boolean skipArt, boolean skipScoring) {
        // Spring Data requires us to persist these variables
        this.testPackageFileName = testPackageFileName;
        this.skipArt = skipArt;
        this.skipScoring = skipScoring;

        //Create steps
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(FILE_UPLOAD, TargetSystem.Internal,"Uploading test package"));
        steps.add(new Step(VALIDATE, TargetSystem.Internal, "Parsing and validating test package"));
        steps.add(new Step(TDS_UPLOAD, TargetSystem.TDS, "Uploading test package to Student and Proctor"));

        if (!skipArt) {
            steps.add(new Step(ART_UPLOAD, TargetSystem.ART,"Uploading test package to ART"));
        }

        if (!skipScoring) {
            steps.add(new Step(TIS_UPLOAD, TargetSystem.TIS, "Uploading test package to TIS"));
            steps.add(new Step(THSS_UPLOAD, TargetSystem.THSS,"Uploading test package to THSS"));
        }

        this.setSteps(steps);
        this.setType(JobType.LOADER);
    }

    public String getTestPackageFileName() {
        return this.testPackageFileName;
    }

    public boolean isSkipArt() {
        return skipArt;
    }

    public boolean isSkipScoring() {
        return skipScoring;
    }

    @Override
    public Status getStatus() {
        if (hasStepWithStatus(Status.FAIL)) {
            return Status.FAIL;
        }

        if (hasStepWithStatus(Status.IN_PROGRESS)) {
            return Status.IN_PROGRESS;
        }

        boolean hasStepsNotStarted = this.getSteps().stream()
            .filter(step -> step.getStatus() == Status.NOT_STARTED)
            .count() == this.getSteps().size();

        if (hasStepsNotStarted) {
            return Status.NOT_STARTED;
        }

        return  Status.SUCCESS;
    }

    private boolean hasStepWithStatus(Status status) {
        return this.getSteps().stream()
            .filter(step -> step.getStatus() == status)
            .findFirst()
            .isPresent();
    }
}