package tds.support.tool.handlers.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.services.loader.TestPackageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageFileHandlerTest {
    private TestPackageFileHandler fileHandler;

    @Mock
    private TestPackageService mockTestPackageService;

    @Mock
    private InputStream mockTestPackageStream;

    @Before
    public void setUp() {
        fileHandler = new TestPackageFileHandler(mockTestPackageService);
    }

    @Test
    public void shouldSaveTestPackage() {
        Step stepToUpdate = new Step();
        Step step = fileHandler.handleTestPackage(stepToUpdate,"jobId", "packageName", mockTestPackageStream, 100L);

        verify(mockTestPackageService).saveTestPackage("jobId", "packageName", mockTestPackageStream, 100L);

        assertThat(step.getDescription()).isEqualTo("Uploading file packageName");
        assertThat(step.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(step.getErrors()).isEmpty();
    }

    @Test
    public void shouldReturnErrorsInStepDuringException() {
        Step stepToUpdate = new Step();
        when(mockTestPackageService.saveTestPackage("jobId", "packageName", mockTestPackageStream, 100L)).thenThrow(new RuntimeException("Fail"));

        Step step = fileHandler.handleTestPackage(stepToUpdate,"jobId", "packageName", mockTestPackageStream, 100L);

        verify(mockTestPackageService).saveTestPackage("jobId", "packageName", mockTestPackageStream, 100L);

        assertThat(step.getDescription()).isEqualTo("Uploading file packageName");
        assertThat(step.getStatus()).isEqualTo(Status.FAIL);
        assertThat(step.getErrors()).hasSize(1);

        Error error = step.getErrors().get(0);
        assertThat(error.getMessage()).isEqualTo("Failed to upload file");
        assertThat(error.getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
    }
}