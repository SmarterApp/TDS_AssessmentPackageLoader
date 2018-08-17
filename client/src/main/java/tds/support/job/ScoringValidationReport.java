package tds.support.job;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to hold the difference between original test results and rescored one.
 */
public class ScoringValidationReport {
    private final String jobId;
    private final Map<String, Object> differenceReport;

    public ScoringValidationReport() {
        this(null, new HashMap<>());
    }

    public ScoringValidationReport(final String jobId, final Map<String, Object> differenceReport) {
        this.jobId = jobId;
        this.differenceReport = differenceReport;
    }

    public String getJobId() {
        return jobId;
    }

    public Map<String, Object> getDifferenceReport() {
        return differenceReport;
    }
}
