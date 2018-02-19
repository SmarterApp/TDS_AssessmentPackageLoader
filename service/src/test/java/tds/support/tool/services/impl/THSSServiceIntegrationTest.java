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

import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.THSSService;
import tds.teacherhandscoring.model.TeacherHandScoringApiResult;
import tds.testpackage.model.TestPackage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestPackageObjectMapperConfiguration.class, SupportToolProperties.class})
public class THSSServiceIntegrationTest {
    THSSService thssService;

    @Autowired
    XmlMapper xmlMapper;

    @Autowired
    @Qualifier("thssObjectMapper")
    ObjectMapper objectMapper;

    SupportToolProperties supportToolProperties;

    RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        supportToolProperties = new SupportToolProperties();
        supportToolProperties.setThssApiUrl("http://localhost:28039/api");
        // can also use mock bin to record http request.
        // ie: https://requestloggerbin.herokuapp.com/bin/72b396ea-f403-41f3-b42a-2447c6e84416/api

        thssService = new THSSService(supportToolProperties, restTemplate, objectMapper);
    }

    /**
     * Integration test with THSS server
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldLoadItemsIntoTHSS() throws Exception {
        InputStream inputStream = THSSServiceIntegrationTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        TeacherHandScoringApiResult teacherHandScoringApiResult = thssService.loadTestPackage(testPackage);
        teacherHandScoringApiResult.getFiles().forEach(res -> { if (!res.getSuccess()) {System.out.println(res.getErrorMessage());} });
    }
}