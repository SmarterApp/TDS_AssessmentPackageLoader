package tds.support.tool.services;

import java.io.InputStream;

import tds.support.job.Job;

/**
 * Service handling the {@link tds.support.job.Job}
 */
public interface JobService {
    /**
     * Starts the test package import job resulting in a job loaded into TDS, TIS, THSS, and ART
     *
     * @param packageName     the test package name
     * @param testPackage     the {@link java.io.InputStream} containing the test package
     * @param testPackageSize the size of the test package
     * @return
     */
    Job startPackageImport(final String packageName, final InputStream testPackage, long testPackageSize);
}
