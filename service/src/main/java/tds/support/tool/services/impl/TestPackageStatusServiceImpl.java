package tds.support.tool.services.impl;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tds.support.job.*;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;
import tds.support.tool.services.TestPackageStatusService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestPackageStatusServiceImpl implements TestPackageStatusService {
    private final TestPackageStatusRepository testPackageStatusRepository;

    // The test package status only cares about the "downstream" systems that will host the test package.  As such, the
    // other job steps/systems (e.g. file upload and internal) are omitted.
    private final Set<TargetSystem> TEST_PACKAGE_TARGET_SYSTEMS = ImmutableSet.of(TargetSystem.TDS,
            TargetSystem.ART,
            TargetSystem.TIS,
            TargetSystem.THSS);

    @Autowired
    public TestPackageStatusServiceImpl(final TestPackageStatusRepository testPackageStatusRepository) {
        this.testPackageStatusRepository = testPackageStatusRepository;
    }

    @Override
    public TestPackageStatus save(final Job job) {
        final List<TestPackageTargetSystemStatus> targetSystems = job.getType() == JobType.LOAD
                ? job.getSteps().stream()
                    .filter(step -> TEST_PACKAGE_TARGET_SYSTEMS.contains(step.getJobStepTarget()))
                    .map(step -> new TestPackageTargetSystemStatus(step.getJobStepTarget(), step.getStatus()))
                    .collect(Collectors.toList())
                : job.getSteps().stream() // If this is a delete/rollback job, remove successfully deleted systems
                    .filter(step -> TEST_PACKAGE_TARGET_SYSTEMS.contains(step.getJobStepTarget()) && step.getStatus() != Status.SUCCESS)
                    .map(step -> new TestPackageTargetSystemStatus(step.getJobStepTarget(), step.getStatus()))
                    .collect(Collectors.toList());

        final TestPackageStatus testPackageStatus = new TestPackageStatus(job.getName(),
                LocalDateTime.now(),
                job.getId(),
                job.getType(),
                targetSystems);

        return testPackageStatusRepository.save(testPackageStatus);
    }

    @Override
    public Page<TestPackageStatus> getAll(final Pageable pageable) {
        return testPackageStatusRepository.findAll(pageable);
    }

    @Override
    public Page<TestPackageStatus> searchByName(final String testPackageNameFragment, final Pageable pageable) {
        return testPackageStatusRepository.findAllByNameContainingIgnoreCase(testPackageNameFragment, pageable);
    }

    @Override
    public void delete(final String testPackageName) {
        testPackageStatusRepository.delete(testPackageName);
    }
}
