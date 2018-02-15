package tds.support.job;

import java.util.ArrayList;
import java.util.List;

public class TestPackageDeleteJob extends Job {
    private static final String TEST_PACKAGE_PREFIX = "test-package";
    public static final String ART_DELETE = TEST_PACKAGE_PREFIX + "-art-delete";
    public static final String TDS_DELETE = TEST_PACKAGE_PREFIX + "-tds-delete";
    public static final String TIS_DELETE = TEST_PACKAGE_PREFIX + "-tis-delete";
    public static final String THSS_DELETE = TEST_PACKAGE_PREFIX + "-thss-delete";
    // These properties are not set explicitly in code - these properties are automatically populated by
    // Spring Data and serialized before being sent to the client.
    private final boolean skipArt;
    private final boolean skipScoring;

    public TestPackageDeleteJob(final String name, boolean skipArt, boolean skipScoring) {
        // Spring Data requires us to persist these variables
        this.setName(name);
        this.skipArt = skipArt;
        this.skipScoring = skipScoring;

        //Create steps
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(TDS_DELETE, TargetSystem.TDS, "Deleting the test package from TDS (Student and Proctor)"));

        if (!skipArt) {
            steps.add(new Step(ART_DELETE, TargetSystem.ART,"Deleting the test package from ART"));
        }

        if (!skipScoring) {
            steps.add(new Step(TIS_DELETE, TargetSystem.TIS, "Deleting the test package from TIS"));
            steps.add(new Step(THSS_DELETE, TargetSystem.THSS,"Deleting the test package from THSS"));
        }

        this.setSteps(steps);
        // Although these steps are functionally the same, we should be explicit about what type of job this is
        this.setType(JobType.DELETE);
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
