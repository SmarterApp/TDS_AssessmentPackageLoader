package tds.testpackage.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;


@RunWith(SpringRunner.class)
public class TestPackageSchemaValidationTests {
    @Test
    public void testSpecificationExampleShouldValidate() throws Exception {
        InputStream inputXmlStream = TestPackageSchemaValidationTests.class.getClassLoader().getResourceAsStream("test-specification-example-1.xml");
        InputStream inputXsdStream = TestPackageSchemaValidationTests.class.getClassLoader().getResourceAsStream("xsd/v3-test-package.xsd");
        Source xmlFile = new StreamSource(inputXmlStream);
        Source xsdFile = new StreamSource(inputXsdStream);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(xsdFile);
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }

    @Test
    public void testSpecificationTHSSExampleShouldValidate() throws Exception {
        InputStream inputXmlStream = TestPackageSchemaValidationTests.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        InputStream inputXsdStream = TestPackageSchemaValidationTests.class.getClassLoader().getResourceAsStream("xsd/v3-test-package.xsd");
        Source xmlFile = new StreamSource(inputXmlStream);
        Source xsdFile = new StreamSource(inputXsdStream);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(xsdFile);

        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }

}
