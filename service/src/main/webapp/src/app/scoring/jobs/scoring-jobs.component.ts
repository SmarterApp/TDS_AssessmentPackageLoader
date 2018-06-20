import {
  Component, EventEmitter, Input, OnDestroy, OnInit, Output, TemplateRef, ViewChild,
  ViewEncapsulation
} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {ScoringJobService} from "./scoring-jobs.service";
import {ScoringJob} from "./model/scoring-job.model";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import 'rxjs/add/operator/takeWhile';
import {AuthGuard} from "../../auth.component";

@Component({
  selector: 'scoring-jobs',
  templateUrl: './scoring-jobs.component.html',
  styleUrls: ['../scoring.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ScoringJobsComponent implements OnInit, OnDestroy {

  @ViewChild('dateTmpl') dateTmpl: TemplateRef<any>;
  @ViewChild('validateTmpl') validateTmpl: TemplateRef<any>;
  @ViewChild('systemTmpl') systemTmpl: TemplateRef<any>;

  // query: LoaderJobsQuery;
  searchTerm: string = '';
  selectedJob: ScoringJob;
  private alive: boolean; // used to unsubscribe from the TimerObservable when OnDestroy is called.
  @Output() selectedScoringJobChange: EventEmitter<ScoringJob> = new EventEmitter<ScoringJob>();
  filteredScoringJobs: ScoringJob[];
  private _scoringJobs: ScoringJob[];
  selected = [];
  columns = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private service: ScoringJobService,
              private authGuard: AuthGuard) {
    this.alive = true;
  }

  get scoringJobs(): ScoringJob[] {
    return this._scoringJobs;
  }

  set scoringJobs(scoringJobs: ScoringJob[]) {
    this._scoringJobs = scoringJobs;
    this.updateFilteredScoringJobs();
  }

  ngOnInit() {
    this.columns = [
      {prop: 'shortId', name: 'Job ID', width: 20},
      {prop: 'examId', name: 'Exam ID', width: 70},
      {prop: 'assessmentId', name: 'Test ID', width: 70},
      {prop: 'studentName', name: 'Student', width: 40},
      {prop: 'createdAt', name: 'Date Created', cellTemplate: this.dateTmpl, width: 35},
      {prop: 'status', name: 'Scoring Status', width: 30},
    ];
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
      .getScoringJobs()
      .subscribe(scoringJobs => {
          this.scoringJobs = scoringJobs;
        },
        error => {
          console.log("scoring-jobs.component got error getting data - refreshing auth");
          this.authGuard.updateUser();
        });
  }

  onSearchChange() {
    this.updateFilteredScoringJobs();
  }

  updateFilteredScoringJobs() {
    // Check if test package name or job id matches
    this.filteredScoringJobs = this.scoringJobs
      .filter(x => x.id.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0
        || x.assessmentId.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0
        || x.studentName.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0
        || x.examId.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0
      );
  }

  onRowSelect(event) {
    this.selectedJob = event['selected'][0];
    this.selectedScoringJobChange.emit(this.selectedJob);
  }

  rowClass = (rowData: ScoringJob) => {
    if (this.selectedJob && this.selectedJob.id === rowData.id) {
      return 'active';
    }

    // return rowData.status = 'FAILED' ? 'failed' : '';
  };

  uploadClick() {
    this.router.navigateByUrl('/scoring/upload');
  }
}
