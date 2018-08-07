package tds.support.tool.model;

import org.springframework.data.annotation.Id;

public class TestResultsMetadata {
    @Id
    private String id;
    private String jobId;
    private String testResultsExamId;

    private TestResultsMetadata() {

    }

    public TestResultsMetadata(final String jobId, final String testResultsExamId) {
        this.jobId = jobId;
        this.testResultsExamId = testResultsExamId;
    }

    /**
     * @return the job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the job id
     */
    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the unique identifier for the metadata
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the unique identifier for the metadata
     */
    public void setId(final String id) {
        this.id = id;
    }


    public String getTestResultsExamId() {
        return testResultsExamId;
    }

    public void setTestResultsExamId(final String testResultsExamId) {
        this.testResultsExamId = testResultsExamId;
    }
}
