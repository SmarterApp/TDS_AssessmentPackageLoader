package tds.support.tool.services.converter.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tds.support.tool.services.converter.TestPackageConverterService;
import tds.support.tool.services.converter.TestPackageMapper;
import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.TestPackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class TestPackageConverterServiceImpl implements TestPackageConverterService {
    private final Logger log = LoggerFactory.getLogger(TestPackageConverterServiceImpl.class);
    private final XmlMapper xmlMapper;

    @Autowired
    public TestPackageConverterServiceImpl(@Qualifier("testSpecificationObjectMapper") final XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public String extractAndConvertTestSpecifications(final String testPackageName, final File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);

        List<Testspecification> specifications = zipFile.stream()
                .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".xml")
                    && !entry.getName().startsWith("__")) // Ignore __MACOSX folder if it exists
                .map(entry -> unzipAndRead(zipFile, entry))
                .collect(Collectors.toList());

        TestPackage testPackage = TestPackageMapper.toNew(testPackageName, specifications);
        return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(testPackage);
    }

    private Testspecification unzipAndRead(final ZipFile zipFile, final ZipEntry entry) {
        try {
            InputStream inputStream = zipFile.getInputStream(entry);
            return xmlMapper.readValue(inputStream, Testspecification.class);
        } catch (IOException e) {
            log.warn("An exception occurred: {}", e);
            throw new RuntimeException(e);
        }
    }
}
