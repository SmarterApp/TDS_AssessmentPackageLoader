package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import tds.support.job.*;
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
            UUID.randomUUID().toString(),
            JobType.LOAD,
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
    public void shouldSaveANewTestPackageStatusRecordWithDeletedSystems() {
        final TestPackageDeleteJob testPackageDeleteJob = new TestPackageDeleteJob("test-package-filename",
                false,
                false);
        Step tdsStep = new Step("tds-delete-step", TargetSystem.TDS, "Description", Status.SUCCESS);
        Step artStep = new Step("art-delete-step", TargetSystem.ART, "Description", Status.FAIL);
        testPackageDeleteJob.setSteps(Arrays.asList(tdsStep, artStep));

        final List<TestPackageTargetSystemStatus> targetSystemStatuses = Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.FAIL)
        );

        final TestPackageStatus expectedTestPackageStatus = new TestPackageStatus("test-package-filename",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.DELETE,
                targetSystemStatuses);

        final ArgumentCaptor<TestPackageStatus> testPackageStatusArgumentCaptor = ArgumentCaptor.forClass(TestPackageStatus.class);

        when(mockTestPackageStatusRepository.save(testPackageStatusArgumentCaptor.capture()))
                .thenReturn(expectedTestPackageStatus);

        testPackageStatusService.save(testPackageDeleteJob);

        final TestPackageStatus capturedTestPackageStatus = testPackageStatusArgumentCaptor.getValue();
        verify(mockTestPackageStatusRepository).save(capturedTestPackageStatus);
        assertThat(capturedTestPackageStatus.getTargets()).hasSize(1);
        assertThat(capturedTestPackageStatus.getTargets().get(0).getTarget()).isEqualTo(TargetSystem.ART);
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
    public void shouldFindAllTestPackageStatusRecordsWithPagination() {
        final List<TestPackageTargetSystemStatus> targetSystemStatuses = Arrays.asList(
            new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS));

        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("first-test-package-filename",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                targetSystemStatuses),
            new TestPackageStatus("second-test-package-filename",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                targetSystemStatuses));

        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage =
            new PageImpl<>(testPackageStatuses, pageRequest, 10);

        when(mockTestPackageStatusRepository.findAll(pageRequest)).thenReturn(testPackageStatusPage);

        final Page<TestPackageStatus> result = testPackageStatusService.getAll(pageRequest);

        verify(mockTestPackageStatusRepository).findAll(pageRequest);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldReturnAPageWithNoContentWhenThereAreNoTestPackageStatusRecords() {
        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(Collections.emptyList(),
            pageRequest,
            10);
        when(mockTestPackageStatusRepository.findAll(pageRequest)).thenReturn(testPackageStatusPage);

        Page<TestPackageStatus> result = testPackageStatusService.getAll(pageRequest);

        verify(mockTestPackageStatusRepository).findAll(pageRequest);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    public void shouldSearchForTestPackageStatusRecordsByName() {
        final String testPackageName = "test-package-filename";
        final List<TestPackageTargetSystemStatus> targetSystemStatuses = Arrays.asList(
            new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
            new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS));
        final TestPackageStatus expectedTestPackageStatus = new TestPackageStatus(testPackageName,
            LocalDateTime.now(),
            UUID.randomUUID().toString(),
            JobType.LOAD,
            targetSystemStatuses);

        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage =
            new PageImpl<>(Collections.singletonList(expectedTestPackageStatus), pageRequest, 10);

        when(mockTestPackageStatusRepository.findAllByNameContainingIgnoreCase(testPackageName, pageRequest))
            .thenReturn(testPackageStatusPage);

        final Page<TestPackageStatus> result = testPackageStatusService.searchByName(testPackageName, pageRequest);


        verify(mockTestPackageStatusRepository).findAllByNameContainingIgnoreCase(testPackageName, pageRequest);
        assertThat(result.getContent()).hasSize(1);
    }
}
