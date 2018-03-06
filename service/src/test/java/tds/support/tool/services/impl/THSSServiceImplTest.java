package tds.support.tool.services.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import tds.common.ValidationError;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.THSSService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class THSSServiceImplTest {
    THSSService thssService;
    SupportToolProperties supportToolProperties;
    XmlMapper xmlMapper;

    @Autowired
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        supportToolProperties = new SupportToolProperties();
        supportToolProperties.setThssApiUrl("http://localhost:28039/api");
        // can also use mock bin to record http request.
        // ie: https://requestloggerbin.herokuapp.com/bin/72b396ea-f403-41f3-b42a-2447c6e84416/api

        // Returns an Http Client that conditionally returns different json responses
        // depending on the contents of the http request body.

        Supplier<CloseableHttpClient> httpClientSupplier = () -> {
            HttpClientMock httpClientMock = new HttpClientMock();
            httpClientMock.onPost("http://localhost:28039/api/item/submit").
                withBody(containsString("success")).
                doReturnJSON("{\n" +
                    "    \"Files\": [\n" +
                    "        {\n" +
                    "            \"FileName\": \"thss-example.json\",\n" +
                    "            \"Success\": true,\n" +
                    "            \"ErrorMessage\": null\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");

            httpClientMock.onPost("http://localhost:28039/api/item/submit").
                withBody(containsString("error")).
                doReturnJSON("{\n" +
                    "    \"Files\": [\n" +
                    "        {\n" +
                    "            \"FileName\": \"thss-example.json\",\n" +
                    "            \"Success\": false,\n" +
                    "            \"ErrorMessage\": \"Mock Error Message\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");


            return httpClientMock;
        };

        thssService = new THSSServiceImpl(httpClientSupplier, supportToolProperties, restTemplate, testPackageObjectMapperConfiguration);
        xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
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