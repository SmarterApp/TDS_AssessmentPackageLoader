package tds.support.tool.services.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.sun.tools.javac.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import tds.support.tool.services.converter.TestPackageMapper;
import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageMapperTest {
    private Testspecification mockPerfAdminLegacyTestPackage;

    private Testspecification mockCATAdminLegacyTestPackage;

    private XmlMapper xmlMapper;

    @Before
    public void deserializeTestPackage() throws IOException {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(xmlModule);
        xmlMapper.registerModule(new Jdk8Module());
        xmlMapper.registerModule(new JaxbAnnotationModule());
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mockPerfAdminLegacyTestPackage = xmlMapper.readValue(this.getClass().getResourceAsStream("/(SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016.xml"),
                Testspecification.class);

        mockCATAdminLegacyTestPackage = xmlMapper.readValue(this.getClass().getResourceAsStream("/(SBAC_PT)SBAC-IRP-CAT-MATH-11-Summer-2015-2016.xml"),
                Testspecification.class);
    }

    @Test
    public void shouldConvertFixedFormTestPackage() throws JsonProcessingException {
        assertThat(mockPerfAdminLegacyTestPackage).isNotNull();
        TestPackage testPackage = TestPackageMapper.toNew("(SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016",
                Collections.singletonList(mockPerfAdminLegacyTestPackage));
        assertThat(testPackage).isNotNull();

        String testPackageXml = xmlMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(testPackage);

        System.out.println(testPackageXml);
    }

    @Test
    public void shouldConvertCATTestPackage() throws JsonProcessingException {
        assertThat(mockPerfAdminLegacyTestPackage).isNotNull();
        TestPackage testPackage = TestPackageMapper.toNew("(SBAC_PT)SBAC-IRP-Perf-MATH-11-Summer-2015-2016",
                Collections.singletonList(mockPerfAdminLegacyTestPackage));
        assertThat(testPackage).isNotNull();

        String testPackageXml = xmlMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(testPackage);

        System.out.println(testPackageXml);
    }

    @Test
    public void shouldConvertCombinedTestPackage() throws JsonProcessingException {
        assertThat(mockPerfAdminLegacyTestPackage).isNotNull();
        TestPackage testPackage = TestPackageMapper.toNew("(SBAC_PT)SBAC-IRP-COMBINED-MATH-11-Summer-2015-2016",
                List.of(mockPerfAdminLegacyTestPackage, mockCATAdminLegacyTestPackage));
        assertThat(testPackage).isNotNull();

        String testPackageXml = xmlMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(testPackage);

        System.out.println(testPackageXml);
    }
}
