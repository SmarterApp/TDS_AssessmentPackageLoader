import { Component, OnInit } from '@angular/core';
import {TestPackageStatusService} from "./test-package-status.service";

@Component({
  selector: 'test-package-status',
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.css'],
  providers: [TestPackageStatusService]
})
export class TestPackageStatusComponent implements OnInit {

  constructor(private _testPackageStatusService: TestPackageStatusService) { }

  ngOnInit() {
  }

}
