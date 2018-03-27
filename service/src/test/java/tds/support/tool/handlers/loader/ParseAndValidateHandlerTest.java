package tds.support.tool.handlers.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import tds.support.job.ErrorSeverity;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.handlers.loader.impl.ParseAndValidateHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.validation.TestPackageValidator;
import tds.support.tool.validation.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseAndValidateHandlerTest {
    private TestPackageHandler handler;
    private TestPackage mockTestPackage;

    @Mock
    private MongoTestPackageRepository mockTestPackageRepository;

    @Mock
    private TestPackageMetadataRepository mockTestPackageMetadataRepository;

    @Mock
    private TestPackageValidator mockValidator;

    @Before
    public void setup() {
        handler = new ParseAndValidateHandler(Collections.singletonList(mockValidator), mockTestPackageRepository, mockTestPackageMetadataRepository);
        mockTestPackage = TestPackage.builder()
                .setAcademicYear("1234")
                .setBankKey(123)
                .setPublishDate("date")
                .setSubject("ELA")
                .setType("summative")
                .setVersion("1")
                .setPublisher("SBAC")
                .setBlueprint(new ArrayList<>())
                .setAssessments(new ArrayList<>())
                .build();
    }

    @Test
    public void shouldPassValidation() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore);
    }

    @Test
    public void shouldFailValidation() {
        // Mock a validation error
        List<ValidationError> errors = new ArrayList<>();
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            List<ValidationError> errors1 = (List<ValidationError>) args[1];
            errors1.add(new ValidationError(ErrorSeverity.CRITICAL, "Oh no!"));
            return null;
        }).when(mockValidator).validate(mockTestPackage, errors);

        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);
        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isGreaterThan(errorsBefore);
    }

    @Test
    public void shouldPassValidationWithWarns() {
        // Mock a validation warn
        List<ValidationError> errors = new ArrayList<>();
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            List<ValidationError> errors1 = (List<ValidationError>) args[1];
            errors1.add(new ValidationError(ErrorSeverity.WARN, "Just a warning"));
            return null;
        }).when(mockValidator).validate(mockTestPackage, errors);

        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);
        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isGreaterThan(errorsBefore);
    }
}
