package tds.support.tool.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Optional;

import tds.common.ValidationError;
import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.THSSService;
import tds.teacherhandscoring.model.TeacherHandScoringApiResult;
import tds.testpackage.model.TestPackage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestPackageObjectMapperConfiguration.class, SupportToolProperties.class})
public class THSSServiceImplIntegrationTest {
    THSSService thssService;

    @Autowired
    XmlMapper xmlMapper;

    @Autowired
    @Qualifier("thssObjectMapper")
    ObjectMapper objectMapper;

    SupportToolProperties supportToolProperties;

    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        supportToolProperties = new SupportToolProperties();
        supportToolProperties.setThssApiUrl("http://localhost:28039/api");
        // can also use mock bin to record http request.
        // ie: https://requestloggerbin.herokuapp.com/bin/72b396ea-f403-41f3-b42a-2447c6e84416/api

        thssService = new THSSServiceImpl(supportToolProperties, objectMapper, restTemplate);
    }

    /**
     * Integration test with THSS server
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldLoadItemsIntoTHSS() throws Exception {
        InputStream inputStream = THSSServiceImplIntegrationTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        Optional<ValidationError> maybeValidationError = thssService.loadTestPackage("thss-test-specification-example-1", testPackage);
        maybeValidationError.ifPresent(res -> System.out.println(res.getMessage()));
    }
}