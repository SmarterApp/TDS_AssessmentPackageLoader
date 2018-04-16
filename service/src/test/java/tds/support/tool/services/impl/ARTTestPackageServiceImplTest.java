package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ARTTestPackageService;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ARTTestPackageServiceImplTest {
    private TestPackage mockTestPackage;
    private ARTTestPackageService service;

    @Mock
    private OAuth2RestTemplate restTemplate;

    @Mock
    private SupportToolProperties supportToolProperties;

    @Before
    public void setup() {
        service = new ARTTestPackageServiceImpl(supportToolProperties, restTemplate);
        mockTestPackage = TestPackage.builder()
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
        final String tenantId = "2112";
        when(supportToolProperties.getArtRestUrl()).thenReturn(Optional.of("http://localhost:8080/rest"));
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/rest/tsbassessment/tenant/" + tenantId)), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(null), HttpStatus.CREATED));

        Optional<ValidationError> maybeError = service.loadTestPackage(tenantId, mockTestPackage);
        assertThat(maybeError).isNotPresent();
        verify(supportToolProperties).getArtRestUrl();
    }

    @Test
    public void shouldReturnErrorsIfFoundInResponse() throws URISyntaxException {
        final String tenantId = "2112";
        when(supportToolProperties.getArtRestUrl()).thenReturn(Optional.of("http://localhost:8080/rest"));
        when(restTemplate.postForEntity(eq(new URI("http://localhost:8080/rest/tsbassessment/tenant/" + tenantId)), isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(new ValidationError("Error", "An Error Occurred")), HttpStatus.UNPROCESSABLE_ENTITY));

        Optional<ValidationError> maybeError = service.loadTestPackage(tenantId, mockTestPackage);
        assertThat(maybeError).isPresent();

        ValidationError error = maybeError.get();
        assertThat(error.getMessage()).isEqualTo("An Error Occurred");
        verify(supportToolProperties).getArtRestUrl();
    }

    @Test
    public void shouldDeleteTestPackageSuccessfully() throws URISyntaxException {
        when(supportToolProperties.getArtRestUrl()).thenReturn(Optional.of("http://localhost:8080/rest"));
        service.deleteTestPackage(mockTestPackage);
        verify(supportToolProperties).getArtRestUrl();
        verify(restTemplate).delete(new URI("http://localhost:8080/rest/tsbassessment"));
    }
}
