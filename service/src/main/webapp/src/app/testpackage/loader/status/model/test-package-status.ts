import {TargetSystemStatus} from "./target-system-status";

/**
 * The representation of a {TestPackageStatus} object from the server.
 */
export interface TestPackageStatus {
  /**
   * @return {string} the unique identifier of the {@link TestPackageStatus}.  In this case, it will be the name of the
   * test package.
   */
  name: string;

  /**
   * @return {Date} The date when the test package was uploaded via the Support Tool
   */
  uploadedAt: Date;

  /**
   * @return {TargetSystemStatus[]} A collection of {@link TargetSystemStatus}es describing the state of the test
   * package in each system.
   */
  targets: TargetSystemStatus[];
}
