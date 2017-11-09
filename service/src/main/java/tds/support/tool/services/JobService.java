package tds.support.tool.services;

import java.io.InputStream;

import tds.support.job.Job;

public interface JobService {
    Job startPackageImport(final String packageName, final InputStream testPackage, long testPackageSize);
}
