package tds.support.tool.model;

import org.springframework.data.annotation.Id;

public class TestPackageMetadata {
    @Id
    private String id;
    private String fileLocation;
    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(final String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
