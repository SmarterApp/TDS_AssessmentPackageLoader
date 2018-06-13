package tds.support.tool.services.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import tds.common.ValidationError;
import tds.support.tool.services.THSSService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class THSSServiceImplTest {
    @TestConfiguration
    @Import({SupportToolTestConfiguration.class})
    static class THSSServiceConfiguration {
    }

    @Autowired
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

    @Autowired
    THSSService thssService;

    @Autowired
    RestTemplate integrationRestTemplate;

    @Autowired
    SupportToolTestConfiguration supportToolTestConfiguration;

    XmlMapper xmlMapper;
    private MockRestServiceServer mockServer;

    @Before
    public void setup() throws Exception {
        xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
        mockServer = supportToolTestConfiguration.setUpMockServer();
    }

    /**
     * Integration test with THSS server
     * @throws Exception
     */
    @Test
    public void shouldLoadItemsIntoTHSSWithoutError() throws Exception {


        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        Optional<ValidationError> maybeValidationError = thssService.loadTestPackage("thss-test-specification-success-1", testPackage);
        maybeValidationError.ifPresent(res -> System.out.println(res.getMessage()));
    }

    @Test
    public void shouldConvertTestPackageToThssConfig() throws Exception {
        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        List<TeacherHandScoringConfiguration> thssConfiguration = thssService.getThssConfiguration(testPackage);


        assertThat(thssConfiguration.size()).isEqualTo(1);
    }

    @Test
    public void shouldConvertTestPackageToThssConfigAndHaveCorrectFields() throws Exception {
        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        TeacherHandScoringConfiguration thssConfiguration = thssService.getThssConfiguration(testPackage).get(0);

        assertThat(thssConfiguration.itemId()).isEqualTo("2703");
        assertThat(thssConfiguration.bankKey()).isEqualTo("187");
        assertThat(thssConfiguration.itemType()).isEqualTo("WER");
        assertThat(thssConfiguration.subject()).isEqualTo("ELA");
        assertThat(thssConfiguration.grade()).isEqualTo("11");
        assertThat(thssConfiguration.handScored()).isEqualTo("1");
    }
}