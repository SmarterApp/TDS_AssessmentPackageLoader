package tds.support.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents the state of a Test Package that is managed by the Support Tool.
 */
public class TestPackageStatus {
    @Id
    private String id;
    private String name;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime uploadedAt = LocalDateTime.now();
    private Set<TestPackageTargetSystemStatus> targets;

    /**
     * Private constructor for frameworks
     */
    private TestPackageStatus() {

    }

    public TestPackageStatus(final String id,
                             final String name,
                             final LocalDateTime uploadedAt,
                             final Set<TestPackageTargetSystemStatus> targets) {
        this.id = id;
        this.name = name;
        this.uploadedAt = uploadedAt;
        this.targets = targets;
    }

    /**
     * @return The unique identifier assigned by MongoDB.
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return The name of the test package.
     * <p>
     * This will be the name of the test package XML file that was uploaded.
     * </p>
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return The {@link java.time.LocalDateTime} when the test package XML file was uploaded.
     */
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(final LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    /**
     * @return The collection of {@link tds.support.job.TargetSystem}s and their status that the test package should be
     * loaded into.
     * <p>
     * This collection will only include the desired {@link tds.support.job.TargetSystem}s.  For example, if the
     * user does not want the test package to include scoring, then this collection would only contain
     * {@link tds.support.job.TestPackageTargetSystemStatus}es for TDS and ART.  If the user wants to load the test
     * package into every system, there would be a {@link tds.support.job.TestPackageTargetSystemStatus} for each
     * target system.
     * </p>
     */
    public Set<TestPackageTargetSystemStatus> getTargets() {
        return targets;
    }

    public void setTargets(final Set<TestPackageTargetSystemStatus> targets) {
        this.targets = targets;
    }
}
