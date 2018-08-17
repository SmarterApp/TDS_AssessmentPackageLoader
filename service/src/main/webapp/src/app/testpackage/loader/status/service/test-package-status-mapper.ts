import { TestPackageStatus } from "../model/test-package-status";
import { TestPackageStatusRow, TestPackageStatusRowBuilder } from "../model/test-package-status-row";
import { TargetSystem } from "../model/target-system.enum";
import { StepStatus } from "../../jobs/model/test-package-job.model";
import {TargetSystemStatus} from "../model/target-system-status";

/**
 * Map a collection of TestPackageStatus objects from the server to {TestPackageStatus}es
 */
export class TestPackageStatusRowMapper {

  // Map target system status Success to Success, anything else to NotApplicable.
  static forwardStatusMap(t: TargetSystemStatus): [TargetSystem, StepStatus] {
    if(t.status == StepStatus.Success) {
      return [t.target, StepStatus.Success];
    }
    return [t.target, StepStatus.NotApplicable];
  }

  // Map target system status Success to NotApplicable, anything else to Success.
  // Used to map DELETE and ROLLBACK jobs - Success is removed, else it failed and is still on system.
  static reverseStatusMap(t: TargetSystemStatus): [TargetSystem, StepStatus] {
    if(t.status == StepStatus.Success) {
      return [t.target, StepStatus.NotApplicable];
    }
    return [t.target, StepStatus.Success];
  }

  /**
   * Map a single JSON representation of a test package status from the server to a {TestPackageStatus}.
   *
   * @param status {TestPackageStatus} A single JSON representation of a test package status record from the server
   * @return {TestPackageStatusRow} A {TestPackageStatusRow} describing the state of the test package in the system
   */
  static map(status: TestPackageStatus): TestPackageStatusRow {
    let mapper = TestPackageStatusRowMapper.forwardStatusMap;
    if(status.jobType == 'DELETE' || status.jobType == 'ROLLBACK') {
      // Reverse the sense of DELETE and ROLLBACK jobs - SUCCESS means not present on these operations.
      mapper = TestPackageStatusRowMapper.reverseStatusMap;
    }
    // Map the target system to its status. Anything other than Success maps to NotApplicable.
    const systemStatusMap = new Map(status.targets.map(mapper));

    // Success means the package is present on the system, even for DELETE and ROLLBACK, due to reverse map.
    // If a system does not exist in the map, then the test package has not been loaded into it.
    return new TestPackageStatusRowBuilder(status.name)
      .withUploadedAt(status.uploadedAt)
      .withJobId(status.jobId)
      .withJobType(status.jobType)
      .withTdsStatus(systemStatusMap.get(TargetSystem.TDS) || StepStatus.NotApplicable)
      .withArtStatus(systemStatusMap.get(TargetSystem.ART) || StepStatus.NotApplicable)
      .withTisStatus(systemStatusMap.get(TargetSystem.TIS) || StepStatus.NotApplicable)
      .withThssStatus(systemStatusMap.get(TargetSystem.THSS) || StepStatus.NotApplicable)
      .build();
  }
}
