package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
import tds.support.tool.services.TISTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Service
public class TISTestPackageServiceImpl implements TISTestPackageService {
    private final RestTemplate restTemplate;
    private final SupportToolProperties properties;

    @Autowired
    public TISTestPackageServiceImpl(final RestTemplate restTemplate,
                                     final SupportToolProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<TestPackage> entity = new HttpEntity<>(testPackage, headers);

        final UriComponentsBuilder builder =
                UriComponentsBuilder
                        .fromHttpUrl(String.format("%s/testpackage", properties.getTisApiUrl().get()));

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
        testPackage.getAssessments().forEach(assessment -> {
            final UriComponentsBuilder builder =
                    UriComponentsBuilder
                            .fromHttpUrl(String.format("%s/assessments/%s",
                                    properties.getTisApiUrl().get(),
                                    assessment.getKey()));

            restTemplate.delete(builder.build().toUri());
        });
    }
}
