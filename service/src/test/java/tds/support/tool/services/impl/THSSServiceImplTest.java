package tds.support.tool.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import tds.common.ValidationError;
import tds.support.tool.configuration.S3Properties;
import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.configuration.SupportToolServiceConfiguration;
import tds.support.tool.configuration.security.WebSecurityConfiguration;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.security.SamlUserDetailsServiceImpl;
import tds.support.tool.security.permission.PermissionService;
import tds.support.tool.services.THSSService;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.teacherhandscoring.model.TeacherHandScoringConfiguration;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {
    "tds.support.tool.testpackage.configuration",
    "tds.support.tool.handlers.loader",
    "tds.support.tool.services.loader",
    "tds.support.tool.handlers.loader.impl",
    "tds.support.tool.services.impl",
    "tds.support.tool.validation",
    "tds.support.tool.configuration",
    "tds.support.tool.repositories.loader",
    "tds.support.tool.repositories.loader.impl",
    "tds.support.tool.messaging"})
@Import({SupportToolServiceConfiguration.class, SupportToolProperties.class})
@DataMongoTest
public class THSSServiceImplTest {
    @Autowired
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

    @Autowired
    THSSService thssService;

    @Autowired
    RestTemplate integrationRestTemplate;

    MockRestServiceServer mockServer;
    XmlMapper xmlMapper;
    ObjectMapper objectMapper;

    @MockBean
    AmazonS3 amazonS3;
    @MockBean
    TestPackageRepository testPackageRepository;
    @MockBean
    MessagingService messagingService;
    @MockBean
    RabbitTemplate rabbitTemplate;
    @MockBean
    PermissionService permissionService;
    @MockBean
    WebSecurityConfiguration webSecurityConfiguration;
    @MockBean
    ConnectionFactory connectionFactory;

    @TestConfiguration
    public static class THSSServiceConfiguration {
        @Autowired
        TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

        @Autowired
        RestTemplate integrationRestTemplate;

        @Bean
        @Primary
        public THSSService mockService() {
            SupportToolProperties supportToolProperties = new SupportToolProperties();
            supportToolProperties.setThssApiUrl("http://localhost:28039/api");
            supportToolProperties.setContentUrl("http://localhost:32848");
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

            return new THSSServiceImpl("/tds/bank/items/Item-%1$s-%2$s/item-%1$s-%2$s.xml", httpClientSupplier, supportToolProperties, integrationRestTemplate, testPackageObjectMapperConfiguration);
        }
    }

    static String RUBRIC_LIST =
        "<rubriclist>\n" +
        "   <rubric scorepoint=\"4\">\n"  +
        "       <name>        Rubric 4</name>\n"  +
        "       <val>\n"  +
        "           <![CDATA[<p style=\"\">&#xA0;</p><p><dl><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">a)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Inference here.</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">b)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Text-supported example: “....” (paragraph reference)</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">c)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Inference here.</p></dd><dt style=\"float: left; margin-top: 1px; margin-bottom: -1em;\">d)</dt><dd style=\"float: top;padding-left:1.5000em; padding-left:1.5000em; \"><p style=\"font-style:normal; font-weight:normal; \">Text-supported example: “....” (paragraph reference)</p></dd></dl></p>]]>\n"  +
        "       </val>\n"  +
        "   </rubric>\n"  +
        "   <samplelist maxval=\"4\" minval=\"4\">\n"  +
        "       <sample purpose=\"OtherExemplar\" scorepoint=\"4\">\n"  +
        "           <name>4-Point Other Official Sample Answers      </name>\n"  +
        "           <annotation />\n"  +
        "           <samplecontent>\n"  +
        "               <![CDATA[<p style=\"\">&#xA0;</p>]]>\n"  +
        "           </samplecontent>\n"  +
        "       </sample>\n"  +
        "   </samplelist>\n" +
        "</rubriclist>";

    @Before
    public void setup() throws Exception {
        integrationRestTemplate.setInterceptors(Arrays.asList());
        mockServer = MockRestServiceServer.createServer(integrationRestTemplate);
        xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
        objectMapper = testPackageObjectMapperConfiguration.getThssObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(Optional.of(RUBRIC_LIST));
        mockServer.expect(method(HttpMethod.GET)).andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON_UTF8));
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