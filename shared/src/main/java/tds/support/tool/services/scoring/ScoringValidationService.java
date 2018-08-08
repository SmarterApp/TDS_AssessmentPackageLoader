package tds.support.tool.services.scoring;

import tds.support.job.ScoringValidationReport;
import tds.trt.model.TDSReport;

/**
 * Service for comparing two TRTs for difference.
 */
public interface ScoringValidationService {
    /**
     * Compares two TRT and returns a difference report.
     *
     * @param jobId the job id associated with this scoring validation job
     * @param original the original TRT
     * @param rescored the rescored TRT
     * @return a difference report detailing the changes between the two TRTs
     */
    ScoringValidationReport validateScoring(String jobId, TDSReport original, TDSReport rescored);
}
