package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import tds.teacherhandscoring.model.RubricList;
import tds.teacherhandscoring.model.RubricListSerializer;
import tds.teacherhandscoring.model.TeacherHandScoring;
import tds.teacherhandscoring.model.TeacherHandScoringIntegration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TeacherHandScoringTests {
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;

    @Before
    public void setUp() {
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new Jdk8Module());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        SimpleModule module = new SimpleModule();
        module.addSerializer(RubricList.class, new RubricListSerializer(this.xmlMapper));
        objectMapper.registerModule(module);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    }

    @Test
    public void TeacherHandScoringShouldDeserialize() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        assertThat(testPackage.getPublisher()).isEqualTo("SBAC_PT");
        assertThat(testPackage.getSubject()).isEqualTo("ELA");
        assertThat(testPackage.getAssessments().get(0).
            getSegments().get(0).
            getSegmentForms().get(0).
            getItemGroups().get(0).
            getItems().get(0).
            getTeacherHandScoring().get().
            getRubricList().rubricOrSamplelist().size()).isEqualTo(10);
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
        System.out.println(json);
    }

    @Test
    public void TeacherHandScoringShouldSerializeToJson() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("rubric-list-example-1.xml");

        RubricList rubricList = xmlMapper.readValue(inputStream, RubricList.class);
        String dimensions = Resources.toString(TeacherHandScoringTests.class.getClassLoader().getResource("dimensions.json"), UTF_8);
        TeacherHandScoring teacherHandScoring = TeacherHandScoring.builder().
            setBaseUrl("C:\\\\src\\\\tss\\\\Item-Manager\\\\OH_ Items").
            setDescription("Mandatory Financial Literacy Classes - SBAC_Field").
            setExemplar("G3_2703_TM.pdf").
            setTrainingGuide("G3_2703_SG.pdf").
            setRubricList(rubricList).
            setDimensions(dimensions).
            build();

        String json = objectMapper.writeValueAsString(teacherHandScoring);
        System.out.println(json);
    }

    @Test
    public void TeacherHandScoringShouldSerializeToJsonForTHSSIntegration() throws Exception {
        InputStream inputStream = TeacherHandScoringTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        TeacherHandScoring teacherHandScoring = testPackage.getAssessments().get(0).
            getSegments().get(0).
            getSegmentForms().get(0).
            getItemGroups().get(0).
            getItems().get(0).
            getTeacherHandScoring().get();
        TeacherHandScoringIntegration teacherHandScoringIntegration = new TeacherHandScoringIntegration(teacherHandScoring);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(teacherHandScoringIntegration);
        System.out.println(json);
    }
}
