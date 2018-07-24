package tds.support.tool.services.scoring.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.model.TestResultsMetadata;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.repositories.scoring.TestResultsMetadataRepository;
import tds.support.tool.services.scoring.TestResultsService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.trt.model.TDSReport;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class TestResultsServiceImpl implements TestResultsService {
    private final TestResultsMetadataRepository testResultsMetadataRepository;
    private final MongoTestResultsRepository mongoTestResultsRepository;
    private final Unmarshaller trtUnmarshaller;
    private final Validator schemaValidator;

    public TestResultsServiceImpl(final TestResultsMetadataRepository testResultsMetadataRepository,
                                  final MongoTestResultsRepository mongoTestResultsRepository,
                                  final TestPackageObjectMapperConfiguration configuration) throws SAXException, JAXBException {
        this.testResultsMetadataRepository = testResultsMetadataRepository;
        this.mongoTestResultsRepository = mongoTestResultsRepository;
        this.trtUnmarshaller = configuration.getTestResultsUnmarshaller();
        this.schemaValidator = configuration.getTestResultsSchemaValidator();
    }

    @Override
    public TestResultsMetadata saveTestResults(final TestResultsScoringJob job, final String testResultsName,
                                               final InputStream testResultsInputStream, final long testResultsSize) {
        final TDSReport testResults = createTdsReport(testResultsInputStream, job.getId(), testResultsName);

        final TestResultsWrapper savedTestResults = mongoTestResultsRepository.save(new TestResultsWrapper(job.getId(), testResults));
        final TestResultsMetadata metadata = new TestResultsMetadata(job.getId(), savedTestResults.getMongoId());

        // Update TRT Load Job specific fields
        job.setExamId(testResults.getOpportunity().getKey());
        job.setAssessmentId(testResults.getTest().getTestId());
        job.setStudentName(getFullName(testResults.getExaminee()));

        return testResultsMetadataRepository.save(metadata);
    }

    @Override
    public void saveRescoredTestResults(String jobId, String rescoredTrtString) {
        final TestResultsWrapper testResults = mongoTestResultsRepository.findByJobId(jobId);

        if (testResults == null) {
            throw new RuntimeException("No stored test results for job id " + jobId);
        }

        final InputStream inputStream = new ByteArrayInputStream(rescoredTrtString.getBytes());
        TDSReport rescoredTrt = createTdsReport(inputStream, jobId, "Re-score results");

        testResults.setRescoredTestResults(rescoredTrt);

        // TODO: produce difference report and save it as well.

        mongoTestResultsRepository.save(testResults);

    }

    private TDSReport createTdsReport(InputStream testResultsInputStream, String jobId, String testResultsName) {
        try {
            // Create a copy of the input stream (since it can only be read from once, and we
            // need it twice (for persistence and deserialization)
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copy(testResultsInputStream, os);
            byte[] bytes = os.toByteArray();
            final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            // Validate the TRT XML against the schema XSD
            final StreamSource xmlFile = new StreamSource(is);
            schemaValidator.validate(xmlFile);
            // Reset the input stream so it can be read from again
            is.reset();

            return (TDSReport) trtUnmarshaller.unmarshal(is);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Exception occurred while de-serializing test results transmission file. JobID: %s, " +
                            "Package Name: %s", jobId, testResultsName), e);
        } catch (SAXException | JAXBException e) {
            throw new RuntimeException(
                    String.format("Exception occurred while validating the test results transmission file. JobID: %s, " +
                            "Package Name: %s, Validation Error: %s", jobId, testResultsName, e.getMessage()), e);
        }
    }


    private String getFullName(final TDSReport.Examinee examinee) {
        final String firstName = examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attribute -> attribute instanceof TDSReport.Examinee.ExamineeAttribute)
                .map(attribute -> ((TDSReport.Examinee.ExamineeAttribute) attribute))
                .filter(attribute -> attribute.getName().equalsIgnoreCase("FirstName"))
                .map(TDSReport.Examinee.ExamineeAttribute::getValue)
                .findFirst()
                .orElse("");

        final String lastName = examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attribute -> attribute instanceof TDSReport.Examinee.ExamineeAttribute)
                .map(attribute -> ((TDSReport.Examinee.ExamineeAttribute) attribute))
                .filter(attribute -> attribute.getName().equalsIgnoreCase("LastOrSurname"))
                .map(TDSReport.Examinee.ExamineeAttribute::getValue)
                .findFirst()
                .orElse("");

        return String.format("%s, %s", lastName, firstName);
    }
}
