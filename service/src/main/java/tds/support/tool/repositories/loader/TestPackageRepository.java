package tds.support.tool.repositories.loader;

import java.io.InputStream;

public interface TestPackageRepository {
    String savePackage(final String jobId, final String packageName, final InputStream inputStream, long testPackageSize);
}
