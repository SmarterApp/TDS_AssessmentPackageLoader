package tds.support.tool.services.impl;

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
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TDSTestPackageServiceImplTest {
    private TDSTestPackageService service;
    private TestPackage mockTestPackage;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupportToolProperties supportToolProperties;

    @Before
    public void setup() {
        service = new TDSTestPackageServiceImpl(restTemplate, supportToolProperties);
        mockTestPackage = TestPackage.builder()
                .setId("TestPackageId")
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
    }

    @Test
    public void shouldLoadTestPackageSuccessfully() throws URISyntaxException {
        when(supportToolProperties.getAssessmentUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/assessments/TestPackageName")), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(null), HttpStatus.CREATED));

        Optional<ValidationError> maybeError = service.loadTestPackage("TestPackageName.xml", mockTestPackage);
        assertThat(maybeError).isNotPresent();
        verify(supportToolProperties).getAssessmentUrl();
    }

    @Test
    public void shouldReturnErrorsIfFoundInResponse() throws URISyntaxException {
        when(supportToolProperties.getAssessmentUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/assessments/TestPackageName")), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(new ValidationError("Error", "An Error Occurred")), HttpStatus.UNPROCESSABLE_ENTITY));

        Optional<ValidationError> maybeError = service.loadTestPackage("TestPackageName.xml", mockTestPackage);
        assertThat(maybeError).isPresent();

        ValidationError error = maybeError.get();
        assertThat(error.getMessage()).isEqualTo("An Error Occurred");
        verify(supportToolProperties).getAssessmentUrl();
    }

    @Test
    public void shouldDeleteTestPackageSuccessfully() throws URISyntaxException {
        when(supportToolProperties.getAssessmentUrl()).thenReturn("http://localhost:8080");
        service.deleteTestPackage(mockTestPackage);
        verify(supportToolProperties).getAssessmentUrl();
        verify(restTemplate).delete(new URI("http://localhost:8080/SBAC/assessments"));
    }
}
