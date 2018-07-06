package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.model.TestResultsWrapper;
import tds.support.tool.services.ExamItemRescoreService;
import tds.trt.model.TDSReport;

import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.Optional;

@Service
public class ExamItemRescoreServiceImpl implements ExamItemRescoreService {
    private final RestTemplate restTemplate;
    private final SupportToolProperties properties;

    @Autowired
    public ExamItemRescoreServiceImpl(final @Qualifier("jaxbRestTemplate") RestTemplate jaxbRestTemplate,
                                      final SupportToolProperties properties) {
        this.restTemplate = jaxbRestTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<ValidationError> rescoreItems(final String examId, final TestResultsWrapper testResultsWrapper) throws JAXBException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<TDSReport> entity = new HttpEntity<>(testResultsWrapper.getTestResults(), headers);
        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/exam/%s/scores/rescore",
                                properties.getExamUrl(),
                                examId));

        final ResponseEntity<NoContentResponseResource> responseEntity =
                restTemplate.exchange(builder.build().toUri(), HttpMethod.PUT, entity, NoContentResponseResource.class);

        if (responseEntity.getBody() != null
                && responseEntity.getBody().getErrors() != null
                && responseEntity.getBody().getErrors().length > 0) {
            return Optional.of(responseEntity.getBody().getErrors()[0]);
        }

        return Optional.empty();
    }
}
