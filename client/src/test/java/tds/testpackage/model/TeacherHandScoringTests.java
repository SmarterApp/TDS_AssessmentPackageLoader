package tds.testpackage.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Resources;
import net.javacrumbs.jsonunit.JsonAssert;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xmlunit.builder.Input;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.RawValue;
import tds.teacherhandscoring.model.TeacherHandScoring;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

@RunWith(SpringJUnit4ClassRunner.class)
public class TeacherHandScoringTests {
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;

    public TeacherHandScoringTests() {
        TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration = new TestPackageObjectMapperConfiguration();
        xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
        objectMapper = testPackageObjectMapperConfiguration.getThssObjectMapper();
    }

    @Test
    public void TeacherHandScoringShouldDeserialize() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        assertThat(testPackage.getPublisher()).isEqualTo("SBAC_PT");
        assertThat(testPackage.getSubject()).isEqualTo("ELA");
    }

    @Test
    public void TeacherHandScoringShouldDeserializeFromXmlThenSerializeToJson() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        TeacherHandScoring teacherHandScoring = testPackage.getAssessments().get(0).
            getSegments().get(0).
            getSegmentForms().get(0).
            getItemGroups().get(0).
            getItems().get(0).
            getTeacherHandScoring().get();
        String json = objectMapper.writeValueAsString(teacherHandScoring);
    }

    @Test
    public void TeacherHandScoringShouldSerializeToJsonWithoutError() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("rubric-list-example-1.xml");
        String dimensions = Resources.toString(TeacherHandScoringTests.class.getClassLoader().getResource("dimensions.json"), UTF_8);

        TeacherHandScoring teacherHandScoring = TeacherHandScoring.builder().
            setBaseUrl("C:\\\\src\\\\tss\\\\Item-Manager\\\\OH_ Items").
            setDescription("Mandatory Financial Literacy Classes - SBAC_Field").
            setExemplar("G3_2703_TM.pdf").
            setTrainingGuide("G3_2703_SG.pdf").
            setDimensions(Optional.of(new RawValue(dimensions))).
            build();

        objectMapper.writeValueAsString(teacherHandScoring);
    }

    @Test
    public void TeacherHandScoringShouldDeserializeForTHSSIntegration() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        TeacherHandScoring teacherHandScoring = testPackage.getAssessments().get(0).
                getSegments().get(0).
                getSegmentForms().get(0).
                getItemGroups().get(0).
                getItems().get(0).
                getTeacherHandScoring().get();
        TeacherHandScoringConfiguration teacherHandScoringConfiguration = new TeacherHandScoringConfiguration(teacherHandScoring);

        assertThat(teacherHandScoringConfiguration.itemId()).isEqualTo("2703");
        assertThat(teacherHandScoringConfiguration.bankKey()).isEqualTo("187");
        assertThat(teacherHandScoringConfiguration.itemType()).isEqualTo("WER");
        assertThat(teacherHandScoringConfiguration.subject()).isEqualTo("ELA");
        assertThat(teacherHandScoringConfiguration.grade()).isEqualTo("11");
        assertThat(teacherHandScoringConfiguration.handScored()).isEqualTo("1");
    }

    @Test
    public void TestPackageWithTeacherHandScoringShouldDeserializeFromJson() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(testPackage);
        System.out.println(json);
        TestPackage jsonTestPackage = objectMapper.readValue(json, TestPackage.class);
        String json2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonTestPackage);
        System.out.println();
        System.out.println(json2);
        assertJsonEquals(json, json2);
    }

    @Test
    @Ignore
    public void TeacherHandScoringShouldSerializeToJsonForTHSSIntegration() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        Path path = Paths.get(getClass().getResource("/thss-example-1.json").toURI());
        String expectedJSON = new String(Files.readAllBytes(path));

        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        TeacherHandScoring teacherHandScoring = testPackage.getAssessments().get(0).
            getSegments().get(0).
            getSegmentForms().get(0).
            getItemGroups().get(0).
            getItems().get(0).
            getTeacherHandScoring().get();
        TeacherHandScoringConfiguration teacherHandScoringConfiguration = new TeacherHandScoringConfiguration(teacherHandScoring);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Arrays.asList(teacherHandScoringConfiguration));

        assertJsonEquals(expectedJSON, json,
            JsonAssert.whenIgnoringPaths("[*].rubriclist")
        );
    }

    @Test
    public void givenStringSource_whenAbleToInput_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";

         MatcherAssert.assertThat(
                Input.fromString(testXml),isSimilarTo(Input.fromString(controlXml)));
    }

    public static class Message {
        public RawValue data;
        public String field;
    }

    @Test
    public void dimensionsDeserialization() throws Exception {
        String fieldValue = "value";
        String jsonValue = "{\"value\":{\"text\":\"123\"}}";
        String jsonMessage = "{\"data\":" + jsonValue + ",\"field\":\"" + fieldValue + "\"}";

        Message message = objectMapper.readValue(jsonMessage, Message.class);

        Assert.assertEquals(jsonValue, message.data.getValue());
        Assert.assertEquals(fieldValue, message.field);
    }

    @Test
    public void dimensionsSerialization() throws Exception {
        String fieldValue = "value";
        String jsonValue = "{\"value\":{\"text\":\"123\"}}";
        String jsonMessage = "{\"data\":" + jsonValue + ",\"field\":\"" + fieldValue + "\"}";

        Message message = new Message();
        message.data = new RawValue(jsonValue);
        message.field = fieldValue;

        Assert.assertEquals(jsonMessage, objectMapper.writeValueAsString(message));
    }

}
