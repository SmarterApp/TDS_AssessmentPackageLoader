package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.services.ExamItemRescoreService;
import tds.trt.model.TDSReport;

import javax.xml.bind.JAXBException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamItemRescoreServiceImplTest {
    private ExamItemRescoreService service;
    private TestResultsWrapper mockTestResults;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupportToolProperties supportToolProperties;

    @Before
    public void setup() {
        service = new ExamItemRescoreServiceImpl(restTemplate, supportToolProperties);
        TDSReport trt = new TDSReport();
        mockTestResults = new TestResultsWrapper("jobId", trt);
    }

    @Test
    public void shouldLoadTestPackageSuccessfully() throws URISyntaxException, JAXBException {
        final String examId = "examId";
        when(supportToolProperties.getExamUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.exchange(eq(new URI("http://localhost:8080/exam/examId/scores/rescore")), eq(HttpMethod.PUT),
                isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(null), HttpStatus.CREATED));

        Optional<ValidationError> maybeError = service.rescoreItems(examId, mockTestResults);
        assertThat(maybeError).isNotPresent();
        verify(supportToolProperties).getExamUrl();
    }

    @Test
    public void shouldReturnErrorsIfFoundInResponse() throws URISyntaxException, JAXBException {
        final String examId = "examId";

        when(supportToolProperties.getExamUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.exchange(eq(new URI("http://localhost:8080/exam/examId/scores/rescore")), eq(HttpMethod.PUT),
                isA(HttpEntity.class), eq(NoContentResponseResource.class)))
                .thenReturn(new ResponseEntity<>(new NoContentResponseResource(new ValidationError("Error", "An Error Occurred")),
                        HttpStatus.UNPROCESSABLE_ENTITY));

        Optional<ValidationError> maybeError = service.rescoreItems(examId, mockTestResults);
        assertThat(maybeError).isPresent();

        ValidationError error = maybeError.get();
        assertThat(error.getMessage()).isEqualTo("An Error Occurred");
        verify(supportToolProperties).getExamUrl();
    }
}
