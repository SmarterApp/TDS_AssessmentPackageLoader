package tds.support.tool.handlers.loader;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;
import tds.common.ValidationError;
import tds.common.web.exceptions.NotFoundException;
import tds.support.job.*;
import tds.support.tool.handlers.loader.impl.TDSDeleteStepHandler;
import tds.support.tool.handlers.loader.impl.TDSLoaderStepHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.ArrayList;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TDSDeleteStepHandlerTest {
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
        handler = new TDSDeleteStepHandler(mockService, mockTestPackageRepository, mockTestPackageMetadataRepository);
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
    public void shouldHandleStepSuccessfullyForRollbackJob() {
        TestPackageRollbackJob mockJob = random(TestPackageRollbackJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getParentJobId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getParentJobId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).deleteTestPackage(mockTestPackage);
    }

    @Test
    public void shouldHandleStepSuccessfullyForDeleteJob() {
        TestPackageDeleteJob mockJob = random(TestPackageDeleteJob.class);
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

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).deleteTestPackage(mockTestPackage);
    }

    @Test
    public void shouldCatchExceptionAndFailStep() {
        TestPackageRollbackJob mockJob = random(TestPackageRollbackJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestPackageMetadata mockMetadata = random(TestPackageMetadata.class);

        when(mockTestPackageMetadataRepository.findByJobId(mockJob.getParentJobId())).thenReturn(mockMetadata);
        when(mockTestPackageRepository.findOne(mockMetadata.getTestPackageId())).thenReturn(mockTestPackage);
        doThrow(NotFoundException.class).when(mockService).deleteTestPackage(mockTestPackage);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestPackageMetadataRepository).findByJobId(mockJob.getParentJobId());
        verify(mockTestPackageRepository).findOne(mockMetadata.getTestPackageId());
        verify(mockService).deleteTestPackage(mockTestPackage);
    }
}
