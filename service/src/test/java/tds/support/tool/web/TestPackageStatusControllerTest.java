package tds.support.tool.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.services.TestPackageStatusService;

import static org.assertj.core.api.Assertions.assertThat;
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
            new TestPackageStatus("test package; TDS Only", LocalDateTime.now(), Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("test package: TDS and ART", LocalDateTime.now(), Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        when(mockTestPackageStatusService.getAll()).thenReturn(testPackageStatuses);

        ResponseEntity<List<TestPackageStatus>> response = testPackageStatusController.getAll();

        verify(mockTestPackageStatusService).getAll();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    public void shouldReturnSuccessAndEmptyListWhenThereAreNoTestPackageStatusRecords() {
        when(mockTestPackageStatusService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TestPackageStatus>> response = testPackageStatusController.getAll();

        verify(mockTestPackageStatusService).getAll();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(0);
    }
}
