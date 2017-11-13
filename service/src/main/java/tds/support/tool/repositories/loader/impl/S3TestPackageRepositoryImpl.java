package tds.support.tool.repositories.loader.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

import tds.support.tool.configuration.S3Properties;
import tds.support.tool.repositories.loader.TestPackageRepository;

@Repository
public class S3TestPackageRepositoryImpl implements TestPackageRepository {
    private final Logger log = LoggerFactory.getLogger(S3TestPackageRepositoryImpl.class);
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;

    @Autowired
    S3TestPackageRepositoryImpl(final AmazonS3 s3Client,
                                final S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @Override
    public String savePackage(final String jobId, final String testPackageName, final InputStream testPackageInputStream, final long testPackageSize) {
        String key = s3Properties.getTestPackagePrefix() + "/" + jobId + "/" + testPackageName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(testPackageSize);
        s3Client.putObject(s3Properties.getBucketName(), key, testPackageInputStream, metadata);
        return s3Properties.getBucketName() + "/" + key;
    }
}
