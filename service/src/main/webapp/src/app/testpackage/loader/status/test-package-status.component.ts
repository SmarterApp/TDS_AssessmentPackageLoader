import { Component, OnInit } from '@angular/core';
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatusRow } from "./model/test-package-status-row";
import { StepStatus } from "../jobs/model/test-package-job.model";
import { TargetSystem } from "./model/target-system.enum";


@Component({
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.css', '../../test-package.component.css']
})
export class TestPackageStatusComponent implements OnInit {
  private _testPackageStatuses: TestPackageStatusRow[] = [];

  constructor(private testPackageStatusService: TestPackageStatusService) {
  }

  /**
   * @return {TestPackageStatusRow[]} A collection of {TestPackageStatusRow}s that represent the state of each test
   * package managed by the Support Tool.
   */
  get testPackageStatuses(): TestPackageStatusRow[] {
    return this._testPackageStatuses;
  }

  set testPackageStatuses(value: TestPackageStatusRow[]) {
    this._testPackageStatuses = value;
  }

  ngOnInit() {
    this.testPackageStatusService.getAll()
      .subscribe(response => this.testPackageStatuses = response);
  }

  /**
   * Get the appropriate CSS class for the specified {StepStatus}
   *
   * @param {StepStatus} status The {StepStatus} to evaluate
   * @return {string} The CSS class that represents the specified {StepStatus}
   */
  getStatusIconCss(status: StepStatus): string {
    return TestPackageStatusRow.getStatusIconClass(status);
  }

  /**
   * Build a tooltip description message for the {TargetSystem}'s {StepStatus}
   *
   * @param {StepStatus} status The target system's {StepStatus}
   * @param {TargetSystem} system The {TargetSystem}
   * @return {string} A message describing what the status icon means
   */
  getStatusDescription(status: StepStatus, system: TargetSystem): string {
    let statusMessage = `The test package was not loaded into ${ system }`;

    switch (status) {
      case StepStatus.Success:
        statusMessage = `The test package was loaded into ${ system } successfully`;
        break;
      case StepStatus.Fail:
        statusMessage = `The test package could not be loaded into ${ system }`;
        break;
      default:
        break;
    }

    return statusMessage;
  }

  /**
   * Delete a test package from all the systems it has been loaded into
   *
   * @param {string} name The name of the test package to delete
   */
  deleteTestPackage(name: string) {
    // TODO: implement
    console.log("you're about to delete %s.  You sure about that?", name);
  }
}
