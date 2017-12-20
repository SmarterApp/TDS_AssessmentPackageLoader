package tds.support.tool.services.loader.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public TestPackageServiceImpl(final TestPackageRepository testPackageRepository, final TestPackageMetadataRepository testPackageMetadataRepository,
                                  final MongoTestPackageRepository mongoTestPackageRepository,
                                  final XmlMapper xmlMapper) {
        this.testPackageRepository = testPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public TestPackageMetadata saveTestPackage(final String jobId, final String packageName, final InputStream testPackageInputStream, long testPackageSize) {
        String location = testPackageRepository.savePackage(jobId, packageName, testPackageInputStream, testPackageSize);
        try {
            final TestPackage testPackage = xmlMapper.readValue(testPackageInputStream, TestPackage.class);
            mongoTestPackageRepository.save(testPackage);
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Exception occurred while deserializing test package. JobID: %s Package Name: %s", jobId, packageName), e);
        }

        TestPackageMetadata metadata = new TestPackageMetadata();
        metadata.setFileLocation(location);
        metadata.setJobId(jobId);

        return testPackageMetadataRepository.save(metadata);
    }
}
