import {TargetSystemStatus} from "./target-system-status";

/**
 * The UI representation of a TestPackageStatus object from the server.
 */
export class TestPackageStatus {
  private _name: string;
  private _uploadedAt: Date;
  private _targets: TargetSystemStatus[];

  constructor(name: string, uploadedAt: Date, targets: TargetSystemStatus[]) {
    this._name = name;
    this._uploadedAt = uploadedAt;
    this._targets = targets;
  }

  /**
   * @return {string} the unique identifier of the {@link TestPackageStatus}.  In this case, it will be the name of the
   * test package.
   */
  get name(): string {
    return this._name;
  }

  /**
   * @return {Date} The date when the test package was uploaded via the Support Tool
   */
  get uploadedAt(): Date {
    return this._uploadedAt;
  }

  /**
   * @return {TargetSystemStatus[]} A collection of {@link TargetSystemStatus}es describing the state of the test
   * package in each system.
   */
  get targets(): TargetSystemStatus[] {
    return this._targets;
  }
}
