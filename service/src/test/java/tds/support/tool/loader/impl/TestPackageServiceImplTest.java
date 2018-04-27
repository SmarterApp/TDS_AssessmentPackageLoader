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

import org.xml.sax.SAXException;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.services.loader.TestPackageService;
import tds.support.tool.services.loader.impl.TestPackageServiceImpl;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.testpackage.model.TestPackage;

import javax.xml.transform.Source;
import javax.xml.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
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

    @Mock
    private Validator mockValidator;

    @Mock
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

    private ArgumentCaptor<TestPackageMetadata> metadataArgumentCaptor;

    @Before
    public void setUp() throws IOException, SAXException {
        when(testPackageObjectMapperConfiguration.getXmlMapper()).thenReturn(mockXmlObjectMapper);
        when(testPackageObjectMapperConfiguration.getTestPackageSchemaValidator()).thenReturn(mockValidator);
        service = new TestPackageServiceImpl(mockTestPackageRepository, mockTestPackageMetadataRepository,
            mockMongoTestPackageRepository, testPackageObjectMapperConfiguration);
        metadataArgumentCaptor = ArgumentCaptor.forClass(TestPackageMetadata.class);
    }

    @Test
    public void shouldSaveTestPackage() throws IOException, SAXException {
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

        TestPackage mockTestPackage = TestPackage.builder()
                .setAcademicYear("1234")
                .setBankKey(123)
                .setPublishDate("date")
                .setSubject("ELA")
                .setType("summative")
                .setVersion("1")
                .setPublisher("SBAC")
                .setBlueprint(new ArrayList<>())
                .setAssessments(new ArrayList<>())
                .build();
        mockTestPackage.setId(testPackageId);

        when(mockMongoTestPackageRepository.save(isA(TestPackage.class))).thenReturn(mockTestPackage);
        when(mockXmlObjectMapper.readValue(isA(ByteArrayInputStream.class), eq(TestPackage.class))).thenReturn(testPackage);
        when(mockTestPackageMetadataRepository.save(isA(TestPackageMetadata.class))).thenReturn(testPackageMetadata);

        service.saveTestPackage(jobId, packageName, inputStream, testPackageSize);

        verify(mockTestPackageRepository).savePackage(eq(jobId), eq(packageName), isA(ByteArrayInputStream.class),
            eq(testPackageSize));
        verify(mockMongoTestPackageRepository).save(testPackage);
        verify(mockTestPackageMetadataRepository).save(metadataArgumentCaptor.capture());
        verify(mockValidator).validate(isA(Source.class));

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

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeForSAXValidationException() throws IOException, SAXException {
        final String jobId = "MyJobId";
        final String packageName = "MyPackageName";
        final InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8.name()));
        final long testPackageSize = 1337;

        doThrow(SAXException.class).when(mockValidator).validate(isA(Source.class));
        service.saveTestPackage(jobId, packageName, inputStream, testPackageSize);
    }
}
