package tds.support.tool.services;

import tds.common.ValidationError;
import tds.support.tool.model.TestResultsWrapper;

import javax.xml.bind.JAXBException;
import java.util.Optional;

/**
 * A service used to communicate with the ExamService for re-scoring items in the TRT
 */
public interface ExamItemRescoreService {
    /**
     * Re-scores all score-able items in the TRT
     *
     * @param name The name of the test results file being sent
     * @param testResultsWrapper
     * @return
     */
    Optional<ValidationError> rescoreItems(final String name, final TestResultsWrapper testResultsWrapper) throws JAXBException;
}
