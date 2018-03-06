package tds.support.tool.repositories.loader.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.testpackage.model.TestPackage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class MongoDataTest {

    @Autowired
    private MongoTestPackageRepository repository;

    TestPackageObjectMapperConfiguration testPackageObjectMapperConfiguration;

    private XmlMapper xmlMapper;

    public MongoDataTest() {
        testPackageObjectMapperConfiguration = new TestPackageObjectMapperConfiguration();
        this.xmlMapper = testPackageObjectMapperConfiguration.getXmlMapper();
    }

    @Test
    public void shouldSaveTestPackageToMongoDb() throws IOException {
        InputStream inputStream = MongoDataTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);
        repository.save(testPackage);
        TestPackage testPackage2 = repository.findOne(testPackage.getId());
        assertThat(testPackage2.getAssessments().size(), equalTo(testPackage.getAssessments().size()));
    }

}
