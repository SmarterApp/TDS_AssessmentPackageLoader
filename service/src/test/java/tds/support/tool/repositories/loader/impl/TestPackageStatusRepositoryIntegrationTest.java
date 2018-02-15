package tds.support.tool.repositories.loader.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tds.support.job.Job;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPackageStatusRepositoryIntegrationTest {
    @Autowired
    private TestPackageStatusRepository testPackageStatusRepository;

    @Autowired
    private JobRepository jobRepository;

    @Test
    @Ignore("This integration test is intended for generating seed data to conduct end-to-end testing for the UI")
    public void generateTestPackageStatusSeedData() {
        // Had to create and save each job individually.  If all the jobs were handed off to jobRepository#save as a
        // list (e.g. via Arrays#asList, like how the status records are created below), the types were preserved.  In
        // MongoDB, all the TestPackageLoadJobs would end up in a testPackageLoadJob collection, the
        // TestPackageDeleteJobs would end up in a testPackageDeleteJob collection and so on. Since the real application
        // will persist all Jobs to the job collection, the saves need to happen individually (which, coincidentally,
        // is how they'll happen in the real app's behavior anyway).
        final Job firstJob = new TestPackageLoadJob("LOADER for 01 test package; TDS Only", true, true);
        final Job secondJob = new TestPackageLoadJob("LOADER FOR 02 test package: TDS and ART", false, true);
        final Job thirdJob = new TestPackageLoadJob("LOADER FOR 03 test package: all systems", false, false);
        final Job fourthJob = new TestPackageLoadJob("LOADER FOR 04 test package: all systems, some errors", false, false);
        final Job fifthJob = new TestPackageDeleteJob("DELETE for 05 delete me from all systems", false, false);

        final Job firstSavedJob = jobRepository.save(firstJob);
        final Job secondSavedJob = jobRepository.save(secondJob);
        final Job thirdSavedJob = jobRepository.save(thirdJob);
        final Job fourthSavedJob = jobRepository.save(fourthJob);
        final Job fifthSavedJob = jobRepository.save(fifthJob);


        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("LOADER for 01 test package; TDS Only",
                LocalDateTime.now().minus(25, ChronoUnit.MINUTES),
                firstSavedJob.getId(),
                firstSavedJob.getType(),
                Collections.singletonList(new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
                )),
            new TestPackageStatus("LOADER FOR 02 test package: TDS and ART",
                LocalDateTime.now().minus(20, ChronoUnit.MINUTES),
                secondSavedJob.getId(),
                secondSavedJob.getType(),
                Arrays.asList(
                    new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
                )),
            new TestPackageStatus("LOADER FOR 03 test package: all systems",
                LocalDateTime.now().minus(15, ChronoUnit.MINUTES),
                thirdSavedJob.getId(),
                thirdSavedJob.getType(),
                Arrays.asList(
                    new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.SUCCESS)
                )),
            new TestPackageStatus("LOADER FOR 04 test package: all systems, some errors",
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                fourthSavedJob.getId(),
                fourthSavedJob.getType(),
                Arrays.asList(
                    new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.FAIL),
                    new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.FAIL)
                )),
            new TestPackageStatus("DELETE for 05 delete me from all systems",
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES),
                fifthSavedJob.getId(),
                fifthSavedJob.getType(),
                Arrays.asList(
                    new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS),
                    new TestPackageTargetSystemStatus(TargetSystem.TIS, Status.FAIL),
                    new TestPackageTargetSystemStatus(TargetSystem.THSS, Status.FAIL)
                )));

        testPackageStatusRepository.save(testPackageStatuses);
    }
}
