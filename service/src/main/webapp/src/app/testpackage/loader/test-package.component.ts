/**
 * Responsible for controlling the behavior of the test package loader page
 */
import {Component} from "@angular/core";
import {TestPackageJob} from "./jobs/model/test-package-job.model";

@Component({
  selector: 'loader',
  templateUrl: './test-package.component.html',
  styleUrls: ['../test-package.component.css']
})
export class TestPackageComponent {
  selectedJob: TestPackageJob;

  selectedTestPackageJobChange($event) {
    this.selectedJob = $event;
  }
}
