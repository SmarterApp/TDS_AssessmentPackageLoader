import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../../shared/data/data.service";
import 'rxjs/add/observable/of';
// import {LoaderJob} from "./model/loader-jobs.model";
// import 'rxjs/add/operator/map';
import {LoaderJob, StepStatus} from "./model/loader-job.model";
import {JobError} from "../../../shared/job-error.model";
import { HttpParams } from '@angular/common/http';

@Injectable()
export class LoaderJobService {
  constructor(private dataService: DataService) {
  }

  getLoaderJobs(): Observable<LoaderJob[]> {

    let params = new HttpParams().append('jobType', 'LOADER');

    return this.dataService
      .get("/load", { params: params })
      .map(jobs => jobs.map(this.mapLoaderJobsFromApi));


    // let error = new JobError();
    // error.message = "This is an error";
    // error.severity = "Error";
    // error.system = 'TIS';
    //
    // let jobs: LoaderJob[] = [];
    // for (var i = 0; i < 30; i++) {
    //   let job = new LoaderJob();
    //   job.id = i;
    //   job.testPackageName = '(SBAC_PT)SBAC-ELA-G7-MATH' + i + '.xml';
    //   job.dateCreated = new Date().toString();
    //   job.type = "Create";
    //
    //   if (i % 2 === 0) {
    //     job.tdsStepStatus = StepStatus.Failed;
    //     job.artStepStatus = StepStatus.Failed;
    //     job.tisStepStatus = StepStatus.InProgress;
    //     job.thssStepStatus = StepStatus.InProgress;
    //   } else {
    //     job.tdsStepStatus = StepStatus.Success;
    //     job.artStepStatus = StepStatus.Success;
    //     job.tisStepStatus = StepStatus.Success;
    //     job.thssStepStatus = StepStatus.Success;
    //   }
    //
    //   job.errors = i % 3 ? [] : [error];
    //
    //   if (i === 7) {
    //     for (var j = 0; j < 20; j++) {
    //       let error2 = new JobError();
    //       error2.message = "This is a really really really long test delivery system error that should go beyond the width of the data grid. Should be fully displayable in a onhover tooltipe";
    //       error2.severity = "Error";
    //       error2.system = 'TDS';
    //       job.tdsStepStatus = StepStatus.Failed;
    //       job.artStepStatus = StepStatus.NotApplicable;
    //       job.tisStepStatus = StepStatus.NotApplicable;
    //       job.thssStepStatus = StepStatus.NotApplicable;
    //       job.errors.push(error2);
    //     }
    //   }
    //
    //   jobs.push(job);
    // }
    //
    // return Observable.of(jobs);
  }

  private mapLoaderJobsFromApi(apiModel): LoaderJob[] {
    console.log(apiModel);
  }
}
