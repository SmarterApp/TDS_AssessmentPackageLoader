import {StepStatus} from "../../jobs/model/test-package-job.model";
import {TargetSystem} from "./target-system.enum";

/**
 * Describes {@link TargetSystem} into which the test package was loaded, the state of the package in that system (either
 * success or failure) and the date/time at which the status was recorded.
 */
export class TargetSystemStatus {
  private _target: TargetSystem;
  private _status: StepStatus;
  private _statusDate: Date;

  constructor(target: TargetSystem, status: StepStatus, statusDate: Date) {
    this._target = target;
    this._status = status;
    this._statusDate = statusDate;
  }

  /**
   * @return {TargetSystem} The {@link TargetSystem} into which the test package was loaded
   */
  get target(): TargetSystem {
    return this._target;
  }

  /**
   * @return {StepStatus} The {@link StepStatus} of the test package in the specified {@link TargetSystem}
   */
  get status(): StepStatus {
    return this._status;
  }

  /**
   * @return {Date} The date/time at which the status record was recorded
   */
  get statusDate(): Date {
    return this._statusDate;
  }
}
