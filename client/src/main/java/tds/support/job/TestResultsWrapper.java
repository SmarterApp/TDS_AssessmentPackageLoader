package tds.support.job;

import org.springframework.data.annotation.Id;
import tds.trt.model.TDSReport;

public class TestResultsWrapper {
    @Id
    private String mongoId;

    private String jobId;

    private TDSReport testResults;

    public TestResultsWrapper(final String jobId, final TDSReport testResults) {
        this.jobId = jobId;
        this.testResults = testResults;
    }

    public String getMongoId() {
        return mongoId;
    }

    public TDSReport getTestResults() {
        return testResults;
    }

    public String getJobId() {
        return jobId;
    }
}
