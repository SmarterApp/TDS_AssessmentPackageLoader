package tds.support.tool.web;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.tool.services.converter.TestPackageConverterService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageConverterController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class, TestPackageObjectMapperConfiguration.class})
public class TestPackageConverterControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @MockBean
    private TestPackageConverterService mockService;
    private String mockTestPackageBlob;

    @Before
    public void setup() throws IOException {
        mockTestPackageBlob = IOUtils.toString(
                this.getClass().getResourceAsStream("/V3-(SBAC_PT)IRP-GRADE-11-MATH-EXAMPLE.xml"), "UTF-8");
    }

    @Test
    public void shouldConvertTestPackageSuccessfully() throws Exception {
        when(mockService.extractAndConvertTestSpecifications(isA(String.class), any()))
                .thenReturn(mockTestPackageBlob);
        MockMultipartFile mockFile = new MockMultipartFile("file", "testpackage.zip", "multipart/form-data", "<TestPackage/>".getBytes());

        http.perform(MockMvcRequestBuilders.fileUpload("/api/convert/new")
                .file(mockFile)
                .param("testPackageName", "testPackageFilename"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockTestPackageBlob));
    }

    @Test
    public void shouldReturnUnprocessableEntityForBadZip() throws Exception {
        when(mockService.extractAndConvertTestSpecifications(isA(String.class), any()))
                .thenThrow(IOException.class);
        MockMultipartFile mockFile = new MockMultipartFile("file", "testpackage.something", "multipart/form-data", "<TestPackage/>".getBytes());

        http.perform(MockMvcRequestBuilders.fileUpload("/api/convert/new")
                .file(mockFile)
                .param("testPackageName", "testPackageFilename"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturn500ForMissingFile() throws Exception {
        http.perform(MockMvcRequestBuilders.fileUpload("/api/convert/new")
                .param("testPackageName", "testPackageFilename"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturn500ForMissingTestPackageName() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testpackage.something", "multipart/form-data", "<TestPackage/>".getBytes());

        http.perform(MockMvcRequestBuilders.fileUpload("/api/convert/new")
                .file(mockFile))
                .andExpect(status().isInternalServerError());
    }
}
