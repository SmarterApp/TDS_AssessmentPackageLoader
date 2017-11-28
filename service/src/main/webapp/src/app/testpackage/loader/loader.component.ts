/**
 * Responsible for controlling the behavior of the test package loader page
 */
import {Component} from "@angular/core";
import {LoaderJob} from "./jobs/model/loader-job.model";

@Component({
  selector: 'loader',
  templateUrl: './loader.component.html'
})
export class LoaderComponent {
  selectedJob: LoaderJob;

  selectedLoaderJobChange($event) {
    this.selectedJob = $event;
  }
}
