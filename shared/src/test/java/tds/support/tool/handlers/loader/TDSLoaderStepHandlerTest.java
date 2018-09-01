package tds.support.tool.handlers.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.handlers.loader.impl.TDSLoaderStepHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.ArrayList;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TDSLoaderStepHandlerTest {
    private TestPackageHandler handler;
    private TestPackage mockTestPackage;

    @Mock
    private TDSTestPackageService mockService;

    @Mock
    private MongoTestPackageRepository mockTestPackageRepository;

    @Mock
    private TestPackageMetadataRepository mockTestPackageMetadataRepository;

    @Before
    public void setup() {
        handler = new TDSLoaderStepHandler(mockService, mockTestPackageMetadataRepository, mockTestPackageRepository);
        mockTestPackage = TestPackage.builder()
                .setId("TestPackageId")
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
    public void shouldHandleStepSuccessfully() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(mockJob.getName(), mockTestPackage)).thenReturn(Optional.empty());

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(mockJob.getName(), mockTestPackage);
    }


    @Test
    public void shouldHandleStepSuccessfullyWithWarns() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(mockJob.getName(), mockTestPackage))
                .thenReturn(Optional.of(new ValidationError("WARN", "Just a warning...")));

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isGreaterThan(errorsBefore);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(mockJob.getName(), mockTestPackage);
    }

    @Test
    public void shouldReturnErrorFromServiceCallAndFailStep() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(mockJob.getName(), mockTestPackage))
                .thenReturn(Optional.of(new ValidationError("Some", "Error")));

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(mockJob.getName(), mockTestPackage);
    }

    @Test
    public void shouldCatchExceptionAndFailStep() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(mockJob.getName(), mockTestPackage))
                .thenThrow(HttpClientErrorException.class);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(mockJob.getName(), mockTestPackage);
    }

    @Test
    public void shouldSanitize422Error() {
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(mockJob.getName(), mockTestPackage))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY));

        handler.handle(mockJob, mockStep);

        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);
        assertTrue(mockStep.getErrors().get(errorsAfter-1)
                .getMessage().contains(TDSLoaderStepHandler.ERROR_TEXT_FOR_422_EXCEPTION));
    }
}

