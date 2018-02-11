import { Component, OnInit } from '@angular/core';
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatusRow } from "./model/test-package-status-row";
import { StepStatus } from "../jobs/model/test-package-job.model";


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
   * Delete a test package from all the systems it has been loaded into
   *
   * @param {string} name The name of the test package to delete
   */
  deleteTestPackage(name: string) {
    // TODO: implement
    console.log("you're about to delete %s.  You sure about that?", name);
  }
}
