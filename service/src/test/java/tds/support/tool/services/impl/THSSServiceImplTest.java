package tds.support.tool.services.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.TestPackage;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestPackageObjectMapperConfiguration.class})
public class THSSServiceImplTest {
    @Autowired
    XmlMapper xmlMapper;

    @Test
    public void shouldConvertTestPackageToThssConfig() throws Exception {
        InputStream inputStream = THSSServiceImplIntegrationTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        List<TeacherHandScoringConfiguration> thssConfiguration = THSSServiceImpl.getThssConfiguration(testPackage);

        assertThat(thssConfiguration.size()).isEqualTo(1);
    }

    @Test
    public void shouldConvertTestPackageToThssConfigAndHaveCorrectFields() throws Exception {
        InputStream inputStream = THSSServiceImplIntegrationTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        TeacherHandScoringConfiguration thssConfiguration = THSSServiceImpl.getThssConfiguration(testPackage).get(0);

        assertThat(thssConfiguration.itemId()).isEqualTo("2703");
        assertThat(thssConfiguration.bankKey()).isEqualTo("187");
        assertThat(thssConfiguration.itemType()).isEqualTo("WER");
        assertThat(thssConfiguration.subject()).isEqualTo("ELA");
        assertThat(thssConfiguration.grade()).isEqualTo("11");
        assertThat(thssConfiguration.handScored()).isEqualTo("1");
    }
}