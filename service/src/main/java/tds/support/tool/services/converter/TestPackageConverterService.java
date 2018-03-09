package tds.support.tool.services.converter;

import java.io.File;
import java.io.IOException;

public interface TestPackageConverterService {
    String extractAndConvertTestSpecifications(final String testPackageName, final File file) throws IOException;
}
