package tds.support.tool.services.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.TDSTestPackageService;
import tds.support.tool.utils.TestPackageUtils;
import tds.testpackage.model.TestPackage;

@Service
public class TDSTestPackageServiceImpl implements TDSTestPackageService {
    private final RestTemplate restTemplate;
    private final SupportToolProperties properties;

    @Autowired
    public TDSTestPackageServiceImpl(final RestTemplate integrationRestTemplate,
                                     final SupportToolProperties properties) {
        this.restTemplate = integrationRestTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TestPackage> entity = new HttpEntity<>(testPackage, headers);

        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/assessments/%s",
                                properties.getAssessmentUrl(),
                                FilenameUtils.removeExtension(name)));

        final ResponseEntity<NoContentResponseResource> responseEntity =
                restTemplate.postForEntity(builder.build().toUri(), entity, NoContentResponseResource.class);

        if (responseEntity.getBody() != null
                && responseEntity.getBody().getErrors() != null
                && responseEntity.getBody().getErrors().length > 0) {
            return Optional.of(responseEntity.getBody().getErrors()[0]);
        }

        return Optional.empty();
    }

    @Override
    public void deleteTestPackage(final TestPackage testPackage) {
        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/%s/assessments",
                                properties.getAssessmentUrl(),
                                testPackage.getPublisher()));

        testPackage.getAssessments().forEach(assessment ->
                builder.queryParam("assessmentKey", TestPackageUtils.getAssessmentKey(testPackage, assessment.getId())));

        restTemplate.delete(builder.build().toUri());
    }
}
