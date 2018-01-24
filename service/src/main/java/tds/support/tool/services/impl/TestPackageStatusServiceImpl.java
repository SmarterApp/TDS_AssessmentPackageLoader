package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import tds.support.job.Job;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;
import tds.support.tool.services.TestPackageStatusService;

@Service
public class TestPackageStatusServiceImpl implements TestPackageStatusService {
    private final TestPackageStatusRepository testPackageStatusRepository;

    @Autowired
    public TestPackageStatusServiceImpl(final TestPackageStatusRepository testPackageStatusRepository) {
        this.testPackageStatusRepository = testPackageStatusRepository;
    }

    @Override
    public TestPackageStatus save(final Job job) {
        final Map<TargetSystem, TestPackageTargetSystemStatus> targetSystems = job.getSteps().stream()
            .filter(step -> !step.getName().equals(TestPackageLoadJob.FILE_UPLOAD))
            .map(step -> new TestPackageTargetSystemStatus(step.getJobStepTarget(), step.getStatus()))
            .collect(Collectors.toMap(TestPackageTargetSystemStatus::getTarget, Function.identity()));

        final TestPackageStatus testPackageStatus = new TestPackageStatus(job.getName(),
            LocalDateTime.now(),
            targetSystems);

        return testPackageStatusRepository.save(testPackageStatus);
    }

    @Override
    public List<TestPackageStatus> getAll() {
        return testPackageStatusRepository.findAll();
    }

    @Override
    public void delete(final String testPackageName) {
        testPackageStatusRepository.delete(testPackageName);
    }
}
