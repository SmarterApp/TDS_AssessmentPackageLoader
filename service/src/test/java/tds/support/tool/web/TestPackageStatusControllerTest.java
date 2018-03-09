package tds.support.tool.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.services.TestPackageStatusService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageStatusControllerTest {
    @Mock
    private TestPackageStatusService mockTestPackageStatusService;

    private TestPackageStatusController testPackageStatusController;

    @Before
    public void setup() {
        testPackageStatusController = new TestPackageStatusController(mockTestPackageStatusService);
    }

    @Test
    public void shouldGetAllTestPackageStatuses() {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("test package; TDS Only",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("test package: TDS and ART",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(testPackageStatuses, pageRequest, 10);
        when(mockTestPackageStatusService.getAll(pageRequest)).thenReturn(testPackageStatusPage);

        ResponseEntity<Page<TestPackageStatus>> response = testPackageStatusController.getAllByPage(pageRequest);

        verify(mockTestPackageStatusService).getAll(pageRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(2);
        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(2);
    }

    @Test
    public void shouldReturnSuccessAndEmptyListWhenThereAreNoTestPackageStatusRecords() {
        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(Collections.emptyList(), pageRequest, 10);

        when(mockTestPackageStatusService.getAll(isA(Pageable.class))).thenReturn(testPackageStatusPage);

        ResponseEntity<Page<TestPackageStatus>> response = testPackageStatusController.getAllByPage(pageRequest);

        verify(mockTestPackageStatusService).getAll(pageRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(0);
    }

    @Test
    public void shouldSearchForTestPackageStatusRecordsByName() {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("test package; TDS Only",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("test package: TDS and ART",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(testPackageStatuses, pageRequest, 10);
        when(mockTestPackageStatusService.searchByName("TDS", pageRequest))
            .thenReturn(testPackageStatusPage);

        ResponseEntity<Page<TestPackageStatus>> response = testPackageStatusController.searchByName("TDS",
            pageRequest);

        verify(mockTestPackageStatusService).searchByName("TDS", pageRequest);
        assertThat(response.getBody().getContent()).hasSize(2);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(2);
        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(2);
    }

    @Test
    public void shouldReturnAPageWithNoContentWhenASearchByNameYieldsNoResults() {
        final PageRequest pageRequest = new PageRequest(0, 2);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(Collections.emptyList(),
            pageRequest,
            10);
        when(mockTestPackageStatusService.searchByName("TDS", pageRequest))
            .thenReturn(testPackageStatusPage);

        ResponseEntity<Page<TestPackageStatus>> response = testPackageStatusController.searchByName("TDS",
            pageRequest);

        verify(mockTestPackageStatusService).searchByName("TDS", pageRequest);
        assertThat(response.getBody().getContent()).hasSize(0);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(0);
        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(2);
    }
}
