package tds.support.tool.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.configuration.SupportToolServiceConfiguration;
import tds.support.tool.configuration.security.WebSecurityConfiguration;
import tds.support.tool.repositories.loader.TestPackageRepository;
import tds.support.tool.security.permission.PermissionService;
import tds.support.tool.services.THSSService;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
@TestConfiguration
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class SupportToolTestConfiguration {
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

    @Autowired
    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;
    @Autowired
    RestTemplate integrationRestTemplate;

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

    public MockRestServiceServer setUpMockServer() throws Exception {
        integrationRestTemplate.setInterceptors(Collections.emptyList());

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(integrationRestTemplate).ignoreExpectOrder(true).build();

        ObjectMapper objectMapper = testPackageObjectMapperConfiguration.getThssObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(Optional.of(RUBRIC_LIST));
        mockServer.expect(manyTimes(), method(HttpMethod.GET)).andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON_UTF8));
        mockServer.expect(manyTimes(), method(HttpMethod.POST)).andRespond(withSuccess());
        return mockServer;
    }

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
