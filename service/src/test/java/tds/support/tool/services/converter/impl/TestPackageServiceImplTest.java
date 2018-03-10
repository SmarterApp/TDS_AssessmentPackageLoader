package tds.support.tool.services.converter.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tds.support.tool.TestPackageBaseTest;
import tds.support.tool.services.converter.TestPackageConverterService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.TestPackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageServiceImplTest extends TestPackageBaseTest {
    private TestPackageConverterService service;

    @Mock
    private TestPackageObjectMapperConfiguration mockConfiguration;

    @Mock
    private XmlMapper mockMapper;

    @Mock
    private ObjectWriter mockObjectWriter;

    private File mockFile;

    @Before
    public void setup() throws JsonProcessingException {
        when(mockConfiguration.getLegacyTestSpecXmlMapper()).thenReturn(mockMapper);
        when(mockObjectWriter.writeValueAsString(isA(TestPackage.class))).thenReturn("testpackage");
        when(mockMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockObjectWriter);
        service = new TestPackageConverterServiceImpl(mockConfiguration);
        mockFile = new File("src/test/resources/legacy-combined-testspec.zip");
    }

    @Test
    public void shouldConvertTestPackageSuccessfully() throws IOException {
        final String testPackageName = "A Test Package";
        when(mockMapper.readValue(isA(InputStream.class), eq(Testspecification.class)))
                .thenReturn(mockPerfAdminLegacyTestPackage);
        final String result = service.extractAndConvertTestSpecifications(testPackageName, mockFile);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("testpackage");
        verify(mockMapper, times(2)).readValue(isA(InputStream.class), eq(Testspecification.class));
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionIfInputIsNotAZip() throws IOException {
        final String testPackageName = "A Test Package";
        File file = new File("src/test/resources/(SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016.xml");
        service.extractAndConvertTestSpecifications(testPackageName, file);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionIfZipContainsNoXmlFiles() throws IOException {
        final String testPackageName = "A Test Package";
        File file = new File("src/test/resources/textfile.zip");
        service.extractAndConvertTestSpecifications(testPackageName, file);
    };
}
