package tds.support.tool.repositories.loader.impl;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import tds.support.tool.configuration.S3Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class S3TestPackageRepositoryImplTest {
    @Mock
    private AmazonS3 mockS3Client;

    @Mock
    private InputStream mockTestPackageStream;

    private S3TestPackageRepositoryImpl repository;

    @Before
    public void setUp() {
        final S3Properties properties = new S3Properties();
        properties.setBucketName("testBucket");
        properties.setTestPackagePrefix("support-tool/dev/test-package");

        repository = new S3TestPackageRepositoryImpl(mockS3Client, properties);
    }

    @Test
    public void shouldSaveTestPackageToS3() {
        repository.savePackage("jobId", "testPackageName", mockTestPackageStream, 100);

        ArgumentCaptor<ObjectMetadata> objectMetadataArgumentCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

        verify(mockS3Client).putObject(eq("testBucket"), eq("support-tool/dev/test-package/jobId/testPackageName"), eq(mockTestPackageStream), objectMetadataArgumentCaptor.capture());

        assertThat(objectMetadataArgumentCaptor.getValue().getContentLength()).isEqualTo(100);
    }
}