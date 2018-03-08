package tds.support.tool.services.converter;

import net.lingala.zip4j.exception.ZipException;
import org.springframework.web.multipart.MultipartFile;
import tds.testpackage.legacy.model.Testspecification;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TestPackageConverterService {
    String extractAndConvertTestSpecifications(final String testPackageName, final File file) throws IOException;
}
