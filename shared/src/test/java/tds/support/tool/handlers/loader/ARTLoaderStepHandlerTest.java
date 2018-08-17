package tds.support.tool.handlers.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Optional;

import tds.common.ValidationError;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.handlers.loader.impl.ARTLoaderStepHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.ARTTestPackageService;
import tds.support.tool.services.impl.ProgmanClientServiceImpl;
import tds.testpackage.model.TestPackage;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ARTLoaderStepHandlerTest {
    private TestPackageHandler handler;
    private TestPackage mockTestPackage;

    @Mock
    private ARTTestPackageService mockService;

    @Mock
    private MongoTestPackageRepository mockTestPackageRepository;

    @Mock
    private TestPackageMetadataRepository mockTestPackageMetadataRepository;

    @Mock
    private ProgmanClientServiceImpl mockProgmanClientService;

    @Before
    public void setup() {
        handler = new ARTLoaderStepHandler(mockService, mockTestPackageRepository, mockTestPackageMetadataRepository, mockProgmanClientService);
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
        final String tenantId = "58703df1e4b0f3fb93dba0f3";
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(tenantId, mockTestPackage)).thenReturn(Optional.empty());
        when(mockProgmanClientService.getTenantId()).thenReturn(tenantId);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(tenantId, mockTestPackage);
    }

    @Test
    public void shouldHandleStepSuccessfullyWithWarns() {
        final String tenantId = "58703df1e4b0f3fb93dba0f3";
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(tenantId, mockTestPackage))
                .thenReturn(Optional.of(new ValidationError("WARN", "Just a warning...")));
        when(mockProgmanClientService.getTenantId()).thenReturn(tenantId);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isGreaterThan(errorsBefore);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(tenantId, mockTestPackage);
    }

    @Test
    public void shouldReturnErrorFromServiceCallAndFailStep() {
        final String tenantId = "58703df1e4b0f3fb93dba0f3";
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(tenantId, mockTestPackage))
                .thenReturn(Optional.of(new ValidationError("Some", "Error")));
        when(mockProgmanClientService.getTenantId()).thenReturn(tenantId);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(tenantId, mockTestPackage);
    }

    @Test
    public void shouldCatchExceptionAndFailStep() {
        final String tenantId = "58703df1e4b0f3fb93dba0f3";
        TestPackageLoadJob mockJob = random(TestPackageLoadJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        when(mockService.loadTestPackage(tenantId, mockTestPackage))
                .thenThrow(HttpClientErrorException.class);
        when(mockProgmanClientService.getTenantId()).thenReturn(tenantId);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).loadTestPackage(tenantId, mockTestPackage);
    }
}
