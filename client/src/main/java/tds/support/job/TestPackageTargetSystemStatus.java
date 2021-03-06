package tds.support.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Represents the state of a Test Package within a given {@link tds.support.job.TargetSystem}.
 */
public final class TestPackageTargetSystemStatus {
    private TargetSystem target;
    private Status status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime statusDate = LocalDateTime.now();

    /**
     * Private constructor for frameworks
     */
    private TestPackageTargetSystemStatus() {

    }

    public TestPackageTargetSystemStatus(final TargetSystem target, final Status status) {
        this.target = target;
        this.status = status;
    }

    /**
     * @return The system for which this status is being reported.
     */
    public TargetSystem getTarget() {
        return target;
    }

    public void setTarget(final TargetSystem target) {
        this.target = target;
    }

    /**
     * @return The {@link tds.support.job.Status} describing the state of the effort to load the test package.
     */
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * @return The date/time when the status was recorded.
     */
    public LocalDateTime getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(final LocalDateTime statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Each {@link tds.support.job.TargetSystem} can only be loaded into once.  For example, a
     * {@link tds.support.job.TestPackageStatus} should not have more than one
     * {@link tds.support.job.TestPackageTargetSystemStatus} representing TDS.  Therefore, the target system is what
     * makes this value type "unique".
     *
     * @param o The object being compared
     * @return True if the object's {@link tds.support.job.TargetSystem} is the same as this instance's
     * {@link tds.support.job.TargetSystem}; otherwise false.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TestPackageTargetSystemStatus)) return false;

        final TestPackageTargetSystemStatus that = (TestPackageTargetSystemStatus) o;

        return getTarget() == that.getTarget();
    }

    @Override
    public int hashCode() {
        return getTarget().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestPackageTargetSystemStatus{");
        sb.append("target=").append(target);
        sb.append(", status=").append(status);
        sb.append(", statusDate=").append(statusDate);
        sb.append('}');
        return sb.toString();
    }
}
