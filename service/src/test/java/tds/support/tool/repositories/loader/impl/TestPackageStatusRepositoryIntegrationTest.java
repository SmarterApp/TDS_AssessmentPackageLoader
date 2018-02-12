package tds.support.tool.repositories.loader.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPackageStatusRepositoryIntegrationTest {
    @Autowired
    private TestPackageStatusRepository testPackageStatusRepository;

    @Test
    @Ignore("This integration test is intended for generating seed data to conduct end-to-end testing for the UI")
    public void generateTestPackageStatusSeedData() {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("01 test package; TDS Only", LocalDateTime.now(), Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("02 test package: TDS and ART", LocalDateTime.now(), Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )),
            new TestPackageStatus("03 test package: all systems", LocalDateTime.now(), Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS)
            )),
            new TestPackageStatus("04 test package: all systems, some errors", LocalDateTime.now(), Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.FAIL),
                new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.FAIL)
            )));

        testPackageStatusRepository.save(testPackageStatuses);
    }

    @Test
    public void shouldFindWhatIAmLookingFor() {
        final String searchTerm = "TDS";
        final PageRequest pageRequest = new PageRequest(0, 5);

        final Page<TestPackageStatus> result = testPackageStatusRepository.findAllByNameContaining(searchTerm, pageRequest);

        assertThat(result).isNotNull();
    }
}
