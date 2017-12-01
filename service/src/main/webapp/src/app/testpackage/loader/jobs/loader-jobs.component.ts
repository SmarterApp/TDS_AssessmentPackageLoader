import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {LoaderJobService} from "./loader-jobs.service";
import {LoaderJob} from "./model/loader-job.model";

@Component({
  selector: 'loader-jobs',
  templateUrl: './loader-jobs.component.html'
})
export class LoaderJobsComponent implements OnInit {
  // query: LoaderJobsQuery;
  searchTerm: string = '';
  @Input()
  selectedJob: LoaderJob;
  @Output() selectedLoaderJobChange: EventEmitter<LoaderJob> = new EventEmitter<LoaderJob>();
  filteredLoaderJobs: LoaderJob[];
  private _loaderJobs: LoaderJob[];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private service: LoaderJobService) {
  }

  get loaderJobs(): LoaderJob[] {
    return this._loaderJobs;
  }

  set loaderJobs(loaderJobs: LoaderJob[]) {
    this._loaderJobs = loaderJobs;
    this.updateFilteredLoaderJobs();
  }

  ngOnInit() {
    this.route.params.subscribe((params: any) => {
      //TODO: Here is where we would pass in our query params filters
      this.updateResults();
    });
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
    console.log("onRowSelect: " + event.data.testPackageName);
    this.selectedJob = event.data;
    this.selectedLoaderJobChange.emit(this.selectedJob);
  }

  uploadClick() {
    this.router.navigateByUrl('/loader/upload');
  }
}
