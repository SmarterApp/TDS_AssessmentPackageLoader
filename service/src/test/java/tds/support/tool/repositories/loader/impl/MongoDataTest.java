package tds.support.tool.repositories.loader.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import tds.support.tool.TestPackageObjectMapperConfiguration;
import tds.support.tool.repositories.MongoTestPackageRepository;

import tds.testpackage.model.TestPackage;

@RunWith(SpringRunner.class)
@Import(TestPackageObjectMapperConfiguration.class)
@DataMongoTest
public class MongoDataTest {

    @Autowired
    private MongoTestPackageRepository repository;

    @Autowired
    private XmlMapper xmlMapper;

    @Test
    public void shouldSaveTestPackageToMongoDb() throws IOException {
        InputStream inputStream = MongoDataTest.class.getClassLoader().getResourceAsStream("thss-test-specification-example-1.xml");
        TestPackage testPackage = xmlMapper.readValue(inputStream, TestPackage.class);

        repository.save(testPackage);
    }

}
