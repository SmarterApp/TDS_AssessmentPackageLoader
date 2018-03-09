import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {TestPackageJobService} from "./test-package-jobs.service";
import {TestPackageJob, StepStatus} from "./model/test-package-job.model";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import 'rxjs/add/operator/takeWhile';

@Component({
  selector: 'test-package-jobs',
  templateUrl: './test-package-jobs.component.html',
  styleUrls: ['../../test-package.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TestPackageJobsComponent implements OnInit, OnDestroy {
  // query: LoaderJobsQuery;
  StepStatuses = StepStatus; // Need to include the enum as a property to access it in template
  searchTerm: string = '';
  @Input()
  selectedJob: TestPackageJob;
  private alive: boolean; // used to unsubscribe from the TimerObservable when OnDestroy is called.
  @Output() selectedTestPackageJobChange: EventEmitter<TestPackageJob> = new EventEmitter<TestPackageJob>();
  filteredTestPackageJobs: TestPackageJob[];
  private _testPackageJobs: TestPackageJob[];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private service: TestPackageJobService) {
    this.alive = true;
  }

  get testPackageJobs(): TestPackageJob[] {
    return this._testPackageJobs;
  }

  set testPackageJobs(loaderJobs: TestPackageJob[]) {
    this._testPackageJobs = loaderJobs;
    this.updateFilteredTestPackageJobs();
  }

  ngOnInit() {
    //TODO: Here is where we would pass in our query params filters
    //this.updateResults()
    TimerObservable.create(0, 5000)
      .takeWhile(() => this.alive)
      .subscribe(() => {
        this.updateResults();
      });
  }

  ngOnDestroy() {
    this.alive = false;
  }

  updateResults() {
    this.service
      .getTestPackageJobs()
      .subscribe(loaderJobs => {
        this.testPackageJobs = loaderJobs;
      });
  }

  onSearchChange() {
    this.updateFilteredTestPackageJobs();
  }

  updateFilteredTestPackageJobs() {
    this.filteredTestPackageJobs = this.testPackageJobs
      .filter(x => x.testPackageName.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0);
  }

  onRowSelect(event) {
    this.selectedJob = event.data;
    this.selectedTestPackageJobChange.emit(this.selectedJob);
  }

  uploadClick() {
    this.router.navigateByUrl('/loader/upload');
  }

  statusClick() {
    this.router.navigateByUrl('/loader/status');
  }
}
