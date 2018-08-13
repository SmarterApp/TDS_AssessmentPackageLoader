package tds.support.tool.services.scoring.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.repositories.scoring.TestResultsMetadataRepository;
import tds.support.tool.services.scoring.ScoringValidationService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.trt.model.TDSReport;

import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Greg Charles on 8/12/18.
 */
@RunWith(MockitoJUnitRunner.class)

public class TestResultsServiceImplTest {
    private TestResultsServiceImpl testResultsService;

    @Mock
    private TestResultsMetadataRepository testResultsMetadataRepository;

    @Mock
    private MongoTestResultsRepository mongoTestResultsRepository;

    @Mock
    private ScoringValidationService scoringValidationService;

    @Mock
    private TestPackageObjectMapperConfiguration configuration;

    @Mock
    private Unmarshaller trtUnmarshaller;

    @Mock
    private Validator schemaValidator;

    @Mock
    private TestResultsWrapper testResults;

    private static String JOB_ID = "12345";
    private static String REPORT_XML = "<TDSReport/>";

    @Before
    public void setUp() throws Exception {
        when(configuration.getTestResultsUnmarshaller()).thenReturn(trtUnmarshaller);
        when(configuration.getTestResultsSchemaValidator()).thenReturn((schemaValidator));

        when(testResults.getTestResults()).thenReturn(new TDSReport());
        when(mongoTestResultsRepository.findByJobId(JOB_ID)).thenReturn(testResults);

        testResultsService = new TestResultsServiceImpl(
                testResultsMetadataRepository,
                mongoTestResultsRepository,
                configuration,
                scoringValidationService);
    }

    @Test
    public void shouldSaveValidRescoredTestResults() throws Exception {
        testResultsService.saveRescoredTestResults(JOB_ID, REPORT_XML);

        verify(mongoTestResultsRepository).findByJobId(JOB_ID);
        verify(schemaValidator).validate(any());
        verify(trtUnmarshaller).unmarshal(any(InputStream.class));
        verify(scoringValidationService).validateScoring(eq(JOB_ID), any(), any());

        verify(testResults).setRescoredTestResults(any());
        verify(testResults).clearErrors();
        verify(mongoTestResultsRepository).save(testResults);
    }

    @Test
    public void shouldReportErrorsForInvalidRescoredTestResults() throws IOException, SAXException {
        doThrow(new SAXException()).when(schemaValidator).validate(any());

        testResultsService.saveRescoredTestResults(JOB_ID, REPORT_XML);
        verify(testResults).setInvalidRescoredTestResults(REPORT_XML);
        verify(testResults).setInvalidRescoredTestResults(anyString());
        verify(mongoTestResultsRepository).save(testResults);
    }

}