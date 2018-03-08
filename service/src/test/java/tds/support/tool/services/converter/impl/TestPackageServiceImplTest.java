package tds.support.tool.services.converter.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tds.support.tool.TestPackageBaseTest;
import tds.support.tool.services.converter.TestPackageConverterService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageServiceImplTest extends TestPackageBaseTest {
    private TestPackageConverterService service;

    @Mock
    private XmlMapper xmlMapper;

    @Before
    public void setup() {
        service = new TestPackageConverterServiceImpl(xmlMapper);
    }

    @Test
    public void shouldConvertTestPackageSuccessfully() throws ZipException, IOException {
//        final String testPackageName = "A Test Package";
//        final MultipartFile mockFile = random(MultipartFile.class);
//
//        final String result = service.extractAndConvertTestSpecifications(testPackageName, mockFile);
//        assertThat(result).isNotNull();
    }
}
