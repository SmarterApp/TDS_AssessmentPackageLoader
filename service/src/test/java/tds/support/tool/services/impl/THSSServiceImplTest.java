package tds.support.tool.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import tds.common.ValidationError;
import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.THSSService;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.TestPackage;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestPackageObjectMapperConfiguration.class})
public class THSSServiceImplTest {
    THSSService thssService;
    SupportToolProperties supportToolProperties;

    @Autowired
    XmlMapper xmlMapper;
    @Autowired
    @Qualifier("thssObjectMapper")
    ObjectMapper objectMapper;


    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        supportToolProperties = new SupportToolProperties();
        supportToolProperties.setThssApiUrl("http://localhost:28039/api");
        // can also use mock bin to record http request.
        // ie: https://requestloggerbin.herokuapp.com/bin/72b396ea-f403-41f3-b42a-2447c6e84416/api

        thssService = new THSSServiceImpl(()-> HttpClients.createDefault(), supportToolProperties, objectMapper, restTemplate);
    }

    /**
     * Integration test with THSS server
     * @throws Exception
     */
    @Test
    public void shouldLoadItemsIntoTHSSWithoutError() throws Exception {
        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        Optional<ValidationError> maybeValidationError = thssService.loadTestPackage("thss-test-specification-example-1", testPackage);
        maybeValidationError.ifPresent(res -> System.out.println(res.getMessage()));
    }

    @Test
    public void shouldConvertTestPackageToThssConfig() throws Exception {
        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        List<TeacherHandScoringConfiguration> thssConfiguration = THSSServiceImpl.getThssConfiguration(testPackage);

        assertThat(thssConfiguration.size()).isEqualTo(1);
    }

    @Test
    public void shouldConvertTestPackageToThssConfigAndHaveCorrectFields() throws Exception {
        InputStream inputStream = THSSServiceImplTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
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