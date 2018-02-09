import { TestPackageStatus } from "../model/test-package-status";
import { TargetSystemStatus } from "../model/target-system-status";

/**
 * Map a collection of TestPackageStatus objects from the server to {TestPackageStatus}es
 */
export class TestPackageStatusMapper {
  /**
   * Map a single JSON representation of a test package status from the server to a {TestPackageStatus}.
   *
   * @param result A single JSON representation of a test package status record from the server
   * @return {TestPackageStatus} A {TestPackageStatus} describing the state of the test package in the system
   */
  static map(result): TestPackageStatus {
    let targetSystems = result.targets.map((target) => {
      return new TargetSystemStatus(target.target, target.status, target.statusDate);
    });

    return new TestPackageStatus(result.name, result.uploadedAt, targetSystems);
  }
}
