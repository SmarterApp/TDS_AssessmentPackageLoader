package tds.support.tool.loader.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.services.loader.TestPackageService;
import tds.support.tool.services.loader.impl.TestPackageServiceImpl;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageServiceImplTest {
    private TestPackageService service;

    @Mock
    private TestPackageRepository mockTestPackageRepository;

    @Mock
    private TestPackageMetadataRepository mockTestPackageMetadataRepository;

    @Mock
    private MongoTestPackageRepository mockMongoTestPackageRepository;

    @Mock
    private XmlMapper mockXmlObjectMapper;

    private ArgumentCaptor<TestPackageMetadata> metadataArgumentCaptor;

    @Before
    public void setUp() {
        service = new TestPackageServiceImpl(mockTestPackageRepository, mockTestPackageMetadataRepository,
            mockMongoTestPackageRepository, mockXmlObjectMapper);
        metadataArgumentCaptor = ArgumentCaptor.forClass(TestPackageMetadata.class);
    }

    @Test
    public void shouldSaveTestPackage() throws IOException {
        final String jobId = "MyJobId";
        final String packageName = "MyPackageName";
        final String testPackageId = "TestPackageId";
        final InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8.name()));
        final long testPackageSize = 1337;
        final String location = "/path/to/testpackage";
        final TestPackage testPackage = TestPackage.builder()
            .setPublisher("publisher")
            .setPublishDate("date")
            .setSubject("subject")
            .setBankKey(187)
            .setAcademicYear("1999")
            .setBlueprint(new ArrayList<>())
            .setAssessments(new ArrayList<>())
            .setType("type")
            .setVersion("version")
            .build();
        final TestPackageMetadata testPackageMetadata = new TestPackageMetadata(location, jobId, testPackageId);
        testPackageMetadata.setFileLocation(location);
        testPackageMetadata.setJobId(jobId);

        when(mockTestPackageRepository.savePackage(eq(jobId), eq(packageName), isA(ByteArrayInputStream.class),
            eq(testPackageSize))).thenReturn(location);
        when(mockXmlObjectMapper.readValue(isA(ByteArrayInputStream.class), eq(TestPackage.class))).thenReturn(testPackage);
        when(mockTestPackageMetadataRepository.save(isA(TestPackageMetadata.class))).thenReturn(testPackageMetadata);

        service.saveTestPackage(jobId, packageName, inputStream, testPackageSize);

        verify(mockTestPackageRepository).savePackage(eq(jobId), eq(packageName), isA(ByteArrayInputStream.class),
            eq(testPackageSize));
        verify(mockMongoTestPackageRepository).save(testPackage);
        verify(mockTestPackageMetadataRepository).save(metadataArgumentCaptor.capture());

        TestPackageMetadata metadata = metadataArgumentCaptor.getValue();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getFileLocation()).isEqualTo(location);
        assertThat(metadata.getJobId()).isEqualTo(jobId);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeForIOException() throws IOException {
        final String jobId = "MyJobId";
        final String packageName = "MyPackageName";
        final InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8.name()));
        final long testPackageSize = 1337;

        when(mockTestPackageRepository.savePackage(eq(jobId), eq(packageName), isA(ByteArrayInputStream.class),
            eq(testPackageSize))).thenThrow(IOException.class);
        service.saveTestPackage(jobId, packageName, inputStream, testPackageSize);
    }
}
