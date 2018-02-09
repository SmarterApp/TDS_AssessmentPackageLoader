import { Component, OnInit } from '@angular/core';
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatus } from "./model/test-package-status";
import { TargetSystemStatus } from "./model/target-system-status";


@Component({
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.css']
})
export class TestPackageStatusComponent implements OnInit {
  private _testPackageStatuses: TestPackageStatus[] = [];

  constructor(private testPackageStatusService: TestPackageStatusService) {
  }

  get testPackageStatuses(): TestPackageStatus[] {
    return this._testPackageStatuses;
  }

  set testPackageStatuses(value: TestPackageStatus[]) {
    this._testPackageStatuses = value;
  }

  ngOnInit() {
    this.testPackageStatusService.getAll()
      .subscribe(response => {
        this.testPackageStatuses = response;
      });
  }
}
