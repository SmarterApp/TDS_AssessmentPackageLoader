package tds.testpackage.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BlueprintElementSearchTest {
    private TestPackage testPackage;

    @Before
    public void setup() throws IOException {
        TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration = new TestPackageObjectMapperConfiguration();
        XmlMapper xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
        InputStream inputStream = BlueprintElementSearchTest.class.getClassLoader().getResourceAsStream("test-specification-example-1.xml");
        testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
    }

    @Test
    public void shouldReturnFlatMapOfAllBlueprintElements() {
        Map<String, BlueprintElement> blueprintMap = testPackage.getBlueprintMap();
        assertThat(blueprintMap).isNotNull();
        assertThat(blueprintMap).isNotEmpty();
        assertThat(blueprintMap).hasSize(77);

        BlueprintElement claim2BpEl = blueprintMap.get("2");

        assertThat(claim2BpEl).isNotNull();
        assertThat(claim2BpEl.getType()).isEqualTo("strand");
    }

    @Test
    public void shouldReturnParentIdOfBlueprintElement() {
        final Optional<String> maybeTargetParentId = testPackage.getBlueprintElementParentId("1|G-SRT");

        assertThat(maybeTargetParentId).isPresent();
        assertThat(maybeTargetParentId.get()).isEqualTo("1");

        final Optional<String> maybeClaimParentId = testPackage.getBlueprintElementParentId("1");
        assertThat(maybeClaimParentId).isEmpty(); // Claims do not have parents
    }

    @Test
    public void shouldReturnEmptyForNoExistingBlueprintElement() {
        final Optional<String> maybeParentId = testPackage.getBlueprintElementParentId("foo");
        assertThat(maybeParentId).isEmpty();
    }
}
