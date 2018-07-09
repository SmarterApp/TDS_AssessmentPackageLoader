package tds.support.tool.services.scoring.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.model.TestResultsMetadata;
import tds.support.tool.model.TestResultsWrapper;
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
        try {
            // Create a copy of the input stream (since it can only be read from once, and we need it twice (for persistence and deserialization)
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(testResultsInputStream, baos);
            byte[] bytes = baos.toByteArray();
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            // Validate the TRT XML against the schema XSD
            final StreamSource xmlFile = new StreamSource(bais);
            schemaValidator.validate(xmlFile);
            // Reset the input stream so it can be read from again
            bais.reset();

            final TDSReport testResults = (TDSReport) trtUnmarshaller.unmarshal(bais);
            final TestResultsWrapper savedTestResults = mongoTestResultsRepository.save(new TestResultsWrapper(testResults));
            final TestResultsMetadata metadata = new TestResultsMetadata(job.getId(), savedTestResults.getMongoId());

            // Update TRT Load Job specific fields
            job.setExamId(testResults.getOpportunity().getKey());
            job.setAssessmentId(testResults.getTest().getTestId());
            job.setStudentName(getFullName(testResults.getExaminee()));

            return testResultsMetadataRepository.save(metadata);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Exception occurred while deserializing test results transmission file. JobID: %s, " +
                            "Package Name: %s", job.getId(), testResultsName), e);
        } catch (SAXException | JAXBException e) {
            throw new RuntimeException(
                    String.format("Exception occurred while validating the test results transmission file. JobID: %s, " +
                            "Package Name: %s, Validation Error: %s", job.getId(), testResultsName, e.getMessage()), e);
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
