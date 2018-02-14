package tds.support.tool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tds.support.tool.configuration.SupportToolProperties;
import tds.teacherhandscoring.model.TeacherHandScoring;
import tds.teacherhandscoring.model.TeacherHandScoringIntegration;
import tds.testpackage.model.Assessment;
import tds.testpackage.model.Item;
import tds.testpackage.model.TestPackage;

public class THSSService {
    final private RestTemplate restTemplate;
    final private SupportToolProperties supportToolProperties;

    @Autowired
    public THSSService(final SupportToolProperties supportToolProperties, final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.supportToolProperties = supportToolProperties;
    }

    public void loadTestPackage(final TestPackage testPackage) {
        supportToolProperties.getThssApiUrl().ifPresent(thssUrl -> {
            final UriComponentsBuilder builder =
                UriComponentsBuilder
                    .fromHttpUrl(String.format("%s/items",
                        thssUrl));
            final List<TeacherHandScoring> teacherHandScoringList = testPackage.getAssessments().stream().
                flatMap(assessment -> assessment.getSegments().stream()).
                flatMap(segment -> segment.pool().stream()).
                flatMap(itemGroup -> itemGroup.items().stream()).
                map(Item::getTeacherHandScoring).
                filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

            final List<TeacherHandScoringIntegration> teacherHandScoringIntegrationList = teacherHandScoringList.stream().
                map(TeacherHandScoringIntegration::new).
                collect(Collectors.toList());

            Stream<ResponseEntity<String>> responseEntityStream = teacherHandScoringIntegrationList.stream().
                map(thss -> restTemplate.postForEntity(builder.build().toUri(), thss, String.class));

        });
    }

    public void deleteTestPackage(final TestPackage testPackage) {

    }
}
