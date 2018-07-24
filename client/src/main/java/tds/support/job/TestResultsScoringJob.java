package tds.support.job;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

public class TestResultsScoringJob extends Job {
    private static final String TEST_PACKAGE_PREFIX = "test-results";
    public static final String FILE_UPLOAD = TEST_PACKAGE_PREFIX + "-file-upload";
    public static final String RESCORE = TEST_PACKAGE_PREFIX + "-rescore";
    public static final String SAVE_VALIDATION = TEST_PACKAGE_PREFIX + "-save-validation-report";

    private String examId;
    private String assessmentId;
    private String studentName;

    public TestResultsScoringJob(final String name, final String userName) {
        // Spring Data requires us to persist these variables
        this.setName(FilenameUtils.removeExtension(name));

        //Create steps
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(FILE_UPLOAD, TargetSystem.Internal, "Uploading test results transmission file"));
        steps.add(new Step(RESCORE, TargetSystem.TDS, "Sending the test results to ExamService to re-score"));

        this.setUserName(userName);
        this.setSteps(steps);
        this.setType(JobType.SCORING);
    }

    @Override
    public Status getStatus() {
        if (hasStepWithStatus(Status.FAIL)) {
            return Status.FAIL;
        }

        if (!getStepByName(SAVE_VALIDATION).isPresent()) {
            return Status.IN_PROGRESS;
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

        return Status.SUCCESS;
    }

    public Step addValidationReportStep() {
        Step step = new Step(SAVE_VALIDATION, TargetSystem.TDS,
                "Receive and save re-scored validation results");
        addStep(step);
        return step;
    }

    private boolean hasStepWithStatus(Status status) {
        return this.getSteps().stream()
                .filter(step -> step.getStatus() == status)
                .findFirst()
                .isPresent();
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(final String examId) {
        this.examId = examId;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(final String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(final String studentName) {
        this.studentName = studentName;
    }


}
