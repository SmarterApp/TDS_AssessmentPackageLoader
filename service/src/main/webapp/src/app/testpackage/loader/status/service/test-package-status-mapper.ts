import { TestPackageStatus } from "../model/test-package-status";
import { TargetSystemStatus } from "../model/target-system-status";
import { TestPackageStatusRow, TestPackageStatusRowBuilder } from "../model/test-package-status-row";
import { TargetSystem } from "../model/target-system.enum";
import { StepStatus } from "../../jobs/model/test-package-job.model";

/**
 * Map a collection of TestPackageStatus objects from the server to {TestPackageStatus}es
 */
export class TestPackageStatusRowMapper {
  /**
   * Map a single JSON representation of a test package status from the server to a {TestPackageStatus}.
   *
   * @param result {TestPackageStatus} A single JSON representation of a test package status record from the server
   * @return {TestPackageStatusRow} A {TestPackageStatusRow} describing the state of the test package in the system
   */
  static map(result: TestPackageStatus): TestPackageStatusRow {
    let statusRow = new TestPackageStatusRowBuilder(result.name)
      .withUploadedAt(result.uploadedAt)
      .withTdsStatus(this.getStatusForTargetSystem(result.targets, TargetSystem.TDS))
      .withArtStatus(this.getStatusForTargetSystem(result.targets, TargetSystem.ART))
      .withTisStatus(this.getStatusForTargetSystem(result.targets, TargetSystem.TIS))
      .withThssStatus(this.getStatusForTargetSystem(result.targets, TargetSystem.THSS))
      .build();

    return statusRow;
  }

  /**
   * Find the {StepStatus} for the specified {TargetSystem}.  If there is no record for the requested {TargetSystem}, a
   * {StepStatus.NotApplicable} is returned (because the test package has not been loaded into the requested
   * {TargetSystem}.
   *
   * @param {TargetSystemStatus[]} targets The collection of {TargetSystemStatus}es to evaluate
   * @param {TargetSystem} system The {TargetSystem} to look for
   * @return {StepStatus} The {StepStatus} of the requested {TargetSystem}; {StepStatus.NotApplicable} if the test
   * package does not have a status for the requested system.
   */
  private static getStatusForTargetSystem(targets: TargetSystemStatus[], system: TargetSystem): StepStatus {
    const target = targets.filter(t => t.target === system)[0];

    return target
      ? target.status
      : StepStatus.NotApplicable;
  }
}
