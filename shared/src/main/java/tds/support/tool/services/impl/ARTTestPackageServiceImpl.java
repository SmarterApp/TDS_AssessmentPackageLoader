package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ARTTestPackageService;
import tds.testpackage.model.TestPackage;

@Service
public class ARTTestPackageServiceImpl implements ARTTestPackageService {
    private final SupportToolProperties properties;
    private final OAuth2RestTemplate restTemplate;

    @Autowired
    public ARTTestPackageServiceImpl(final SupportToolProperties properties, final OAuth2RestTemplate oAuth2RestTemplate) {
        this.properties = properties;
        this.restTemplate = oAuth2RestTemplate;
    }

    @Override
    public Optional<ValidationError> loadTestPackage(final String tenantId, final TestPackage testPackage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TestPackage> entity = new HttpEntity<>(testPackage, headers);
        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/tsbassessment/tenant/%s",
                                properties.getArtRestUrl().get(),
                                tenantId));

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
                        .fromHttpUrl(String.format("%s/tsbassessment",
                                properties.getArtRestUrl().get()));

        testPackage.getAssessments().forEach(assessment -> builder.queryParam("assessmentKey", assessment.getKey()));

        restTemplate.delete(builder.build().toUri());
    }
}
