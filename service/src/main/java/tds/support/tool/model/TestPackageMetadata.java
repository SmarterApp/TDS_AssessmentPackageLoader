package tds.support.tool.model;

import org.springframework.data.annotation.Id;

/**
 * Holds test package metadata used throughout the test package data load process
 */
public class TestPackageMetadata {
    @Id
    private String id;
    private String fileLocation;
    private String jobId;
    private String testPackageId;

    private TestPackageMetadata() {

    }

    public TestPackageMetadata(final String fileLocation, final String jobId, final String testPackageId) {
        this.fileLocation = fileLocation;
        this.jobId = jobId;
        this.testPackageId = testPackageId;
    }

    /**
     * @return the job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the job id
     */
    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the unique identifier for the metadata
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the unique identifier for the metadata
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return the file location for the test package
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * @param fileLocation the file location for the test package
     */
    public void setFileLocation(final String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getTestPackageId() {
        return testPackageId;
    }

    public void setTestPackageId(final String testPackageId) {
        this.testPackageId = testPackageId;
    }
}
