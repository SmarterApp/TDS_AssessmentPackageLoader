package tds.support.tool.services.loader;

import java.io.InputStream;

import tds.support.tool.model.TestPackageMetadata;

public interface TestPackageService {
    TestPackageMetadata saveTestPackage(final String jobId, final String packageName, final InputStream testPackageInputStream, long testPackageSize);
}
