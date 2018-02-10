import { StepStatus } from "../../jobs/model/test-package-job.model";
import { TargetSystem } from "./target-system.enum";

/**
 * Describes {@link TargetSystem} into which the test package was loaded, the state of the package in that system (either
 * success or failure) and the date/time at which the status was recorded.
 */
export interface TargetSystemStatus {
  /**
   * @return {TargetSystem} The {@link TargetSystem} into which the test package was loaded
   */
  target: TargetSystem;

  /**
   * @return {StepStatus} The {@link StepStatus} of the test package in the specified {@link TargetSystem}
   */
  status: StepStatus;

  /**
   * @return {Date} The date/time at which the status record was recorded
   */
  statusDate: Date;
}
