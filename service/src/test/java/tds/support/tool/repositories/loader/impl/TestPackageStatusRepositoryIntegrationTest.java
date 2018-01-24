package tds.support.tool.repositories.loader.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tds.support.tool.repositories.loader.TestPackageStatusRepository;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPackageStatusRepositoryIntegrationTest {
    @Autowired
    private TestPackageStatusRepository testPackageStatusRepository;

    @Test
    public void shouldSaveATestPackageStatus() {
        
    }
}
