import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {LoaderJobService} from "./loader-jobs.service";
import {LoaderJob, StepStatus} from "./model/loader-job.model";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import 'rxjs/add/operator/takeWhile';

@Component({
  selector: 'loader-jobs',
  templateUrl: './loader-jobs.component.html'
})
export class LoaderJobsComponent implements OnInit, OnDestroy {
  // query: LoaderJobsQuery;
  StepStatuses = StepStatus; // Need to include the enum as a property to access it in template
  searchTerm: string = '';
  @Input()
  selectedJob: LoaderJob;
  private alive: boolean; // used to unsubscribe from the TimerObservable when OnDestroy is called.
  @Output() selectedLoaderJobChange: EventEmitter<LoaderJob> = new EventEmitter<LoaderJob>();
  filteredLoaderJobs: LoaderJob[];
  private _loaderJobs: LoaderJob[];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private service: LoaderJobService) {
    this.alive = true;
  }

  get loaderJobs(): LoaderJob[] {
    return this._loaderJobs;
  }

  set loaderJobs(loaderJobs: LoaderJob[]) {
    this._loaderJobs = loaderJobs;
    this.updateFilteredLoaderJobs();
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
      .getLoaderJobs()
      .subscribe(loaderJobs => {
        this.loaderJobs = loaderJobs;
      });
  }

  onSearchChange() {
    this.updateFilteredLoaderJobs();
  }

  updateFilteredLoaderJobs() {
    this.filteredLoaderJobs = this.loaderJobs
      .filter(x => x.testPackageName.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0);
  }

  onRowSelect(event) {
    this.selectedJob = event.data;
    this.selectedLoaderJobChange.emit(this.selectedJob);
  }

  uploadClick() {
    this.router.navigateByUrl('/loader/upload');
  }
}
