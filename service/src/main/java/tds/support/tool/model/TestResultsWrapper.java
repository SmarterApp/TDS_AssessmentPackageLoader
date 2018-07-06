package tds.support.tool.model;

import org.springframework.data.annotation.Id;
import tds.trt.model.TDSReport;

public class TestResultsWrapper {
    @Id
    private String mongoId;

    private TDSReport testResults;

    public TestResultsWrapper(final TDSReport testResults) {
        this.testResults = testResults;
    }

    public String getMongoId() {
        return mongoId;
    }

    public TDSReport getTestResults() {
        return testResults;
    }
}
