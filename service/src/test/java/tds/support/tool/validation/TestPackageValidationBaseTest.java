package tds.support.tool.validation;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import tds.testpackage.model.TestPackage;

import java.io.IOException;

public class TestPackageValidationBaseTest {
    XmlMapper testPackageMapper;

    TestPackage validTestPackage;

    @Before
    public void setUp() throws IOException {
        testPackageMapper = new XmlMapper();
        testPackageMapper.registerModule(new Jdk8Module());
        validTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-VALID.xml"),
                TestPackage.class);
    }
}
