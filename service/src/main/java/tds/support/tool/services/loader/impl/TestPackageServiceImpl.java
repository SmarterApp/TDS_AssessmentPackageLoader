package tds.support.tool.services.loader.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.services.loader.TestPackageService;
import tds.testpackage.model.TestPackage;

@Service
public class TestPackageServiceImpl implements TestPackageService {
    private final TestPackageRepository testPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final XmlMapper xmlMapper;

    @Autowired
    public TestPackageServiceImpl(final TestPackageRepository testPackageRepository,
                                  final TestPackageMetadataRepository testPackageMetadataRepository,
                                  final MongoTestPackageRepository mongoTestPackageRepository,
                                  final XmlMapper xmlMapper) {
        this.testPackageRepository = testPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public TestPackageMetadata saveTestPackage(final String jobId, final String packageName, final InputStream testPackageInputStream, long testPackageSize) {
        try {
            // Create a copy of the input stream (since it can only be read from once, and we need it twice (for persistence and deserialization)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(testPackageInputStream, baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            final String location = testPackageRepository.savePackage(jobId, packageName, bais, testPackageSize);
            // Reset the input stream so it can be read from twice
            bais.reset();

            final TestPackage testPackage = xmlMapper.readValue(bais, TestPackage.class);
            TestPackage savedTestPackage = mongoTestPackageRepository.save(testPackage);
            TestPackageMetadata metadata = new TestPackageMetadata(location, jobId, savedTestPackage.getId());
            return testPackageMetadataRepository.save(metadata);
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Exception occurred while deserializing test package. JobID: %s, Package Name: %s", jobId, packageName), e);
        }
    }
}
