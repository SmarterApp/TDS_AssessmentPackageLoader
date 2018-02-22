package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tds.common.ValidationError;
import tds.common.web.resources.NoContentResponseResource;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ARTTestPackageService;
import tds.support.tool.utils.TestPackageUtils;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Service
public class ARTTestPackageServiceImpl implements ARTTestPackageService {
    //TODO: This RestTemplate will need to be changed to an OAuth2RestTemplate once security is in place
    private final RestTemplate restTemplate;
    private final SupportToolProperties properties;

    @Autowired
    public ARTTestPackageServiceImpl(final RestTemplate restTemplate,
                                     final SupportToolProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
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

        testPackage.getAssessments().forEach(assessment ->
                builder.queryParam("assessmentKey", TestPackageUtils.getAssessmentKey(testPackage, assessment.getId())));

        restTemplate.delete(builder.build().toUri());
    }
}
