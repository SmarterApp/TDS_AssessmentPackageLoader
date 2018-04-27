import {Component, Input} from "@angular/core";
import {TestPackageJob} from "./model/test-package-job.model";

@Component({
  selector: 'test-package-job-details',
  templateUrl: './test-package-job-details.component.html'
})
export class TestPackageJobDetailsComponent {
  @Input()
  selectedJob: TestPackageJob;
}
