package tds.support.tool.services;

import java.util.List;

import tds.support.job.Job;
import tds.support.job.TestPackageStatus;

public interface TestPackageStatusService {
    TestPackageStatus create(final Job job, final String testPackageName);
    TestPackageStatus save(final TestPackageStatus testPackageStatus);
    List<TestPackageStatus> getAll();
}
