import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../../shared/data/data.service";
import 'rxjs/add/observable/of';
// import {LoaderJob} from "./model/loader-jobs.model";
// import 'rxjs/add/operator/map';
import {LoaderJob} from "./model/loader-job.model";
import {JobError} from "../../../shared/job-error.model";

@Injectable()
export class LoaderJobService {
  constructor(private dataService: DataService) {
  }

  getLoaderJobs(): Observable<LoaderJob[]> {

    //TODO: Right now, we're just mocking out some data. Here is where we'd hook things up to the backend

    // return this.dataService
    //   .get("/api/load")
    //   .map(jobs => jobs.map(this.mapJobsFromApi));


    let error = new JobError();
    error.message = "This is an error";
    error.severity = "Error";
    error.system = 'TIS';

    let jobs: LoaderJob[] = [];
    for (var i = 0; i < 30; i++) {
      let job = new LoaderJob();
      job.id = i;
      job.testPackageName = '(SBAC_PT)SBAC-ELA-G7-MATH' + i + '.xml';
      job.dateCreated = new Date().toString();
      job.type = "Create";


      job.errors = i % 3 ? [] : [error];

      if (i === 7) {
        for (var j = 0; j < 20; j++) {
          let error2 = new JobError();
          error2.message = "This is a really really really long test delivery system error that should go beyond the width of the data grid. Should be fully displayable in a onhover tooltipe";
          error2.severity = "Error";
          error2.system = 'TDS';
          job.errors.push(error2);
        }
      }

      jobs.push(job);
    }

    return Observable.of(jobs);
  }
}
