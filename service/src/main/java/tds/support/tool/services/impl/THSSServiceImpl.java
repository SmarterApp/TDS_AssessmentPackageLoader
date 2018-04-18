package tds.support.tool.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tds.common.ValidationError;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.THSSService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.RawValue;
import tds.teacherhandscoring.model.TeacherHandScoring;
import tds.teacherhandscoring.model.TeacherHandScoringApiResult;
import tds.teacherhandscoring.model.TeacherHandScoringApiResultFile;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.Item;
import tds.testpackage.model.ItemGroup;
import tds.testpackage.model.Segment;
import tds.testpackage.model.TestPackage;
import tds.testpackage.model.TestPackageDeserializer;

/**
 * Sends the Teacher Hand Scoring Configuration to THSS
 * <p>
 * Given a Test Specification Package,
 * this service sends the Teacher Hand Scoring Configuration portion of the
 * Test Specification Packages (configuration for individual items) to the
 * THSS .NET application.
 * <p>
 * THSS provides an endpoint to add items to it for configuration
 * prior to allow a teacher to hand score scoring.
 * <p>
 * A JSON file is POSTed to the endpoint as a file attachment:  http://host:28039/api/item/submit
 * <p>
 * This service performs the HTTP POST of the json data and returns the response.
 */
@Service
public class THSSServiceImpl implements THSSService {
    final private String itemContentFormat;
    final private String contentUrl;
    final private Supplier<CloseableHttpClient> httpClientSupplier;
    final private String thssUrl;
    final private ObjectMapper objectMapper;
    final private RestTemplate restTemplate;


    @Autowired
    public THSSServiceImpl(@Value("${tds.content.format:/tds/bank/items/Item-%1$s-%2$s/item-%1$s-%2$s.xml}") final String itemContentFormat,
                           final Supplier<CloseableHttpClient> httpClientSupplier,
                           final SupportToolProperties supportToolProperties,
                           final RestTemplate integrationRestTemplate,
                           final TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration) {
        this.itemContentFormat = itemContentFormat;
        this.contentUrl = supportToolProperties.getContentUrl();
        this.thssUrl = supportToolProperties.getThssApiUrl().orElseThrow(() -> new RuntimeException("THSS api url property is not configured"));
        this.restTemplate = integrationRestTemplate;
        this.httpClientSupplier = httpClientSupplier;
        this.objectMapper = testPackageObjectMapperConfiguration.getThssObjectMapper();
    }

    @Override
    public Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage) throws IOException {
        TestPackageDeserializer.setTestPackageParent(testPackage);

        final UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(String.format("%s/item/submit", thssUrl));
        final List<TeacherHandScoringConfiguration> teacherHandScoringConfigurationList = getThssConfiguration(testPackage);
        final String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(teacherHandScoringConfigurationList);

        final TeacherHandScoringApiResult teacherHandScoringApiResult = postTeacherHandScoringConfiguration(builder.build().toUri(), name, json);

        final List<TeacherHandScoringApiResultFile> errors = teacherHandScoringApiResult.getFiles().stream().filter(TeacherHandScoringApiResultFile::hasError).collect(Collectors.toList());

        if (errors.stream().findAny().isPresent()) {
            final String errorMessage = errors.stream().map(TeacherHandScoringApiResultFile::getErrorMessage).collect(Collectors.joining(", "));
            return Optional.of(new ValidationError("Error", errorMessage));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ValidationError> deleteTestPackage(final TestPackage testPackage) {
        TestPackageDeserializer.setTestPackageParent(testPackage);

        final UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(String.format("%s/item/delete", thssUrl));

        final List<TeacherHandScoringConfiguration> teacherHandScoringConfigurationList = getThssConfiguration(testPackage);
        final String items = teacherHandScoringConfigurationList.stream().map(TeacherHandScoringConfiguration::itemId).collect(Collectors.joining(","));
        builder.queryParam("bankKey", testPackage.getBankKey());
        builder.queryParam("items", items);

        final ResponseEntity<TeacherHandScoringApiResultFile> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE, null, TeacherHandScoringApiResultFile.class);

        if (responseEntity.getBody() != null) {
            final TeacherHandScoringApiResultFile body = responseEntity.getBody();
            if (!body.getSuccess()) {
                final String errorMessage = (body.getErrorMessage() != null) ? body.getErrorMessage() : "";
                return Optional.of(new ValidationError("Error", errorMessage));
            }
        }
        return Optional.empty();
    }

    /**
     * Extracts all of the TeacherHandScoringConfiguration elements out of the TestPackage.
     */
    public List<TeacherHandScoringConfiguration> getThssConfiguration(final TestPackage testPackage) {
        final List<TeacherHandScoring> teacherHandScoringList = testPackage.getAssessments().stream().
                flatMap(assessment -> assessment.getSegments().stream()).
                flatMap(THSSServiceImpl::getSegmentItems).
                map(Item::getTeacherHandScoring).
                filter(Optional::isPresent).map(Optional::get).
                map(teacherHandScoring -> loadRubric(teacherHandScoring)).
            collect(Collectors.toList());

        return teacherHandScoringList.stream().
                map(TeacherHandScoringConfiguration::new).
                collect(Collectors.toList());
    }

    private TeacherHandScoring loadRubric(final TeacherHandScoring teacherHandScoring) {
        final int bankKey = teacherHandScoring.getItem().getTestPackage().getBankKey();
        final String itemKey = teacherHandScoring.getItem().getId();
        final String itemPath = String.format(itemContentFormat, bankKey, itemKey);
        final UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(String.format("%s/rubric?itemPath=%s", contentUrl, itemPath));

        final ResponseEntity<Optional<RawValue>> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<Optional<RawValue>>(){});

        final RawValue rubricListXml = responseEntity.getBody().orElse(new RawValue(""));
        return teacherHandScoring.withRubricList(rubricListXml.getValue());
    }

    /**
     * Sends the THSS JSON configuration data to THSS
     * Given the THSS endpoint and JSON data
     */
    private TeacherHandScoringApiResult postTeacherHandScoringConfiguration(final URI uri, final String name, final String json) throws IOException {
        try (CloseableHttpClient httpclient = httpClientSupplier.get()) {
            final HttpPost httppost = new HttpPost(uri);
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", json.getBytes("UTF-8"),
                    ContentType.APPLICATION_JSON, String.format("%s.json", name));
            httppost.setEntity(builder.build());

            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                final HttpStatus httpStatus = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
                if (!httpStatus.is2xxSuccessful()) {
                    throw new HttpClientErrorException(httpStatus, response.getStatusLine().getReasonPhrase(),
                            ByteStreams.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);
                }
                return objectMapper.readValue(response.getEntity().getContent(), TeacherHandScoringApiResult.class);
            }
        }
    }

    /**
     * Utility function to return the items of a segment, regardless if
     * the items are in the item pool or segment form.
     */
    private static Stream<Item> getSegmentItems(final Segment segment) {
        final Stream<ItemGroup> segmentFormItemGroups = segment.segmentForms().stream().
                flatMap(segmentForm -> segmentForm.itemGroups().stream());

        final Stream<ItemGroup> segmentPoolItemGroups = segment.pool().stream();

        return Stream.concat(segmentPoolItemGroups, segmentFormItemGroups).flatMap(itemGroup -> itemGroup.items().stream());
    }
}
