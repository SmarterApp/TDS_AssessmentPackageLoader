import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {LoaderJob} from "./model/loader-job.model";

@Component({
  selector: 'loader-job-details',
  templateUrl: './loader-job-details.component.html'
})
export class LoaderJobDetailsComponent implements OnInit, OnDestroy {
  @Input()
  selectedJob: LoaderJob;

  ngOnInit() {

  }

  ngOnDestroy() {
    // this.unsubscribe();
  }
}
