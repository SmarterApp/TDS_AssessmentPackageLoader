import { Component, OnInit } from '@angular/core';
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatus } from "./model/test-package-status";
import { TargetSystemStatus } from "./model/target-system-status";
import { TestPackageStatusRow } from "./model/test-package-status-row";
import { StepStatus } from "../jobs/model/test-package-job.model";


@Component({
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.css']
})
export class TestPackageStatusComponent implements OnInit {
  private _testPackageStatuses: TestPackageStatusRow[] = [];

  constructor(private testPackageStatusService: TestPackageStatusService) {
  }

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

  getDisplayIcon(status: StepStatus): string {
    return TestPackageStatusRow.getStatusIconClass(status);
  }
}
