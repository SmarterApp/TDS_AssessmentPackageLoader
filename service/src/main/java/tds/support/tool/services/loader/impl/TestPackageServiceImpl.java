package tds.support.tool.services.loader.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.services.loader.TestPackageService;

@Service
public class TestPackageServiceImpl implements TestPackageService {
    private final TestPackageRepository testPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    @Autowired
    public TestPackageServiceImpl(final TestPackageRepository testPackageRepository, final TestPackageMetadataRepository testPackageMetadataRepository) {
        this.testPackageRepository = testPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public TestPackageMetadata saveTestPackage(final String jobId, final String packageName, final InputStream testPackageInputStream) {
        String location = testPackageRepository.savePackage(jobId, packageName, testPackageInputStream);
        TestPackageMetadata metadata = new TestPackageMetadata();
        metadata.setFileLocation(location);
        metadata.setJobId(jobId);

        return testPackageMetadataRepository.save(metadata);
    }
}
