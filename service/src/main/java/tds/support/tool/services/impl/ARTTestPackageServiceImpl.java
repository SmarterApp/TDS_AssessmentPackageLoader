package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ARTTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Service
public class ARTTestPackageServiceImpl implements ARTTestPackageService {
    private final RestTemplate restTemplate;
    private final SupportToolProperties properties;

    @Autowired
    public ARTTestPackageServiceImpl(@Qualifier("integrationRestTemplate") final RestTemplate restTemplate,
                                     final SupportToolProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<ValidationError> loadTestPackage(final String tenantId, final TestPackage testPackage) {
        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/tsbassessment/tenant/%s",
                                properties.getAssessmentUrl(),
                                tenantId));

        final ResponseEntity<NoContentResponseResource> responseEntity =
                restTemplate.postForEntity(builder.build().toUri(), testPackage, NoContentResponseResource.class);

        if (responseEntity.getBody().getErrors().length > 0) {
            return Optional.of(responseEntity.getBody().getErrors()[0]);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ValidationError> deleteTestPackage(final TestPackage testPackage) {
        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/tsbassessment",
                                properties.getArtRestUrl()));

        testPackage.getAssessments().forEach(assessment -> builder.queryParam("assessmentKey", assessment.getKey()));

        restTemplate.delete(builder.build().toUri());

        return Optional.empty();
    }
}
