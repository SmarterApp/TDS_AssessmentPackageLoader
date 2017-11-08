package tds.support.tool.handlers.loader;

import org.springframework.stereotype.Component;

import java.io.InputStream;

import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.services.loader.TestPackageService;

@Component
public class TestPackageFileHandler {
    private final String STEP_MESSAGE = "Uploaded file %s";
    private final TestPackageService testPackageService;

    public TestPackageFileHandler(final TestPackageService testPackageService) {
        this.testPackageService = testPackageService;
    }

    public Step handleTestPackage(final String jobId, final String packageName, final InputStream testPackage) {
        TestPackageMetadata testPackageMetadata = testPackageService.saveTestPackage(jobId, packageName, testPackage);

        return new Step(String.format(STEP_MESSAGE, packageName), Status.SUCCESS);
    }
}
