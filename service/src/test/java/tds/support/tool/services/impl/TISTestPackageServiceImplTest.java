package tds.support.tool.services.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.TISTestPackageService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TISTestPackageServiceImplTest {
    private TISTestPackageService service;
    private TestPackage mockTestPackage;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupportToolProperties supportToolProperties;

    @Mock
    private TestPackageObjectMapperConfiguration objectMapperConfiguration;

    @Mock
    private XmlMapper mockMapper;

    @Before
    public void setup() throws IOException {
        when(mockMapper.writeValueAsString(mockTestPackage)).thenReturn("testPackage");
        when(objectMapperConfiguration.getLegacyTestSpecXmlMapper()).thenReturn(mockMapper);

        service = new TISTestPackageServiceImpl(restTemplate, supportToolProperties, objectMapperConfiguration);
        XmlMapper testPackageMapper = new XmlMapper();
        testPackageMapper.registerModule(new Jdk8Module());
        mockTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-VALID.xml"),
                TestPackage.class);
    }

    @Test
    public void shouldLoadTestPackageSuccessfully() throws URISyntaxException {
        when(supportToolProperties.getTisApiUrl()).thenReturn(Optional.of("http://localhost:8080"));
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/testpackage")), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(null), HttpStatus.CREATED));

        Optional<ValidationError> maybeError = service.loadTestPackage("TestPackageName.xml", mockTestPackage);
        assertThat(maybeError).isNotPresent();
        verify(supportToolProperties).getTisApiUrl();
    }

    @Test
    public void shouldReturnErrorsIfFoundInResponse() throws URISyntaxException {
        when(supportToolProperties.getTisApiUrl()).thenReturn(Optional.of("http://localhost:8080"));
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/testpackage")), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(new ValidationError("Error", "An Error Occurred")), HttpStatus.UNPROCESSABLE_ENTITY));

        Optional<ValidationError> maybeError = service.loadTestPackage("TestPackageName.xml", mockTestPackage);
        assertThat(maybeError).isPresent();

        ValidationError error = maybeError.get();
        assertThat(error.getMessage()).isEqualTo("An Error Occurred");
        verify(supportToolProperties).getTisApiUrl();
    }

    @Test
    public void shouldDeleteTestPackageSuccessfully() throws URISyntaxException {
        when(supportToolProperties.getTisApiUrl() ).thenReturn(Optional.of("http://localhost:8080"));
        service.deleteTestPackage(mockTestPackage);
        verify(supportToolProperties, times(2)).getTisApiUrl();
        verify(restTemplate).delete(new URI("http://localhost:8080/assessments/(SBAC_PT)SBAC-IRP-CAT-MATH-11-EXAMPLE-2017-2018"));
        verify(restTemplate).delete(new URI("http://localhost:8080/assessments/(SBAC_PT)SBAC-IRP-Perf-MATH-11-EXAMPLE-2017-2018"));
    }
}
