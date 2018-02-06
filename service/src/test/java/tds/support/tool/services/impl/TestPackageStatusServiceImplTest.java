package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;
import tds.support.tool.services.TestPackageStatusService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageStatusServiceImplTest {
    @Mock
    private TestPackageStatusRepository mockTestPackageStatusRepository;

    private TestPackageStatusService testPackageStatusService;

    @Before
    public void setup() {
        testPackageStatusService = new TestPackageStatusServiceImpl(mockTestPackageStatusRepository);
    }

    @Test
    public void shouldSaveANewTestPackageStatusRecord() {
        final TestPackageLoadJob testPackageLoadJob = new TestPackageLoadJob("test-package-filename",
            false,
            false);
        final List<TestPackageTargetSystemStatus> targetSystemStatuses = Arrays.asList(
            new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS));

        final TestPackageStatus expectedTestPackageStatus = new TestPackageStatus("test-package-filename",
            LocalDateTime.now(),
            targetSystemStatuses);

        final ArgumentCaptor<TestPackageStatus> testPackageStatusArgumentCaptor = ArgumentCaptor.forClass(TestPackageStatus.class);
        when(mockTestPackageStatusRepository.save(testPackageStatusArgumentCaptor.capture()))
            .thenReturn(expectedTestPackageStatus);

        testPackageStatusService.save(testPackageLoadJob);

        final TestPackageStatus capturedTestPackageStatus = testPackageStatusArgumentCaptor.getValue();
        verify(mockTestPackageStatusRepository).save(capturedTestPackageStatus);
        assertThat(capturedTestPackageStatus.getTargets()).hasSize(4);
        assertThat(capturedTestPackageStatus.getTargets()).isEqualTo(expectedTestPackageStatus.getTargets());
        assertThat(capturedTestPackageStatus.getName()).isEqualTo(expectedTestPackageStatus.getName());
        assertThat(capturedTestPackageStatus.getUploadedAt().isAfter(expectedTestPackageStatus.getUploadedAt()));
    }

    @Test
    public void shouldDeleteATestPackageStatusRecord() {
        final String testPackageStatusName = "delete-me";
        doNothing().when(mockTestPackageStatusRepository).delete(testPackageStatusName);

        testPackageStatusService.delete(testPackageStatusName);

        verify(mockTestPackageStatusRepository).delete(testPackageStatusName);
    }

    @Test
    public void shouldFindAllTestPackageStatusRecords() {
        final List<TestPackageTargetSystemStatus> targetSystemStatuses = Arrays.asList(
            new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS));

        List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("first-test-package-status",
                LocalDateTime.now(),
                targetSystemStatuses),
            new TestPackageStatus("second-test-package-status",
                LocalDateTime.now(),
                targetSystemStatuses));

        when(mockTestPackageStatusRepository.findAll()).thenReturn(testPackageStatuses);

        List<TestPackageStatus> result = testPackageStatusService.getAll();

        verify(mockTestPackageStatusRepository).findAll();
        assertThat(result).hasSize(2);
    }
}
