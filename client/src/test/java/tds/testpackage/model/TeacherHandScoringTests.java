package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TeacherHandScoringTests {
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new Jdk8Module());
    }


    @Test
    public void THSSExampleShouldDeserialize() throws Exception {
        InputStream inputStream = TestPackageSerializationIntegrationTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        assertThat(testPackage.getPublisher()).isEqualTo("SBAC_PT");
        assertThat(testPackage.getSubject()).isEqualTo("MATH");
        assertThat(testPackage.getAssessments().get(0).getSegments().get(0).getPool().get(0).getItems().get(0).getTeacherHandScoring().get().getRubricList().size()).isEqualTo(1);
    }
}
