import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../../shared/data/data.service";
import 'rxjs/add/observable/of';
import {TestPackageJob, StepStatus} from "./model/test-package-job.model";
import {JobError} from "../../../shared/job-error.model";
import {HttpParams} from '@angular/common/http';

@Injectable()
export class TestPackageJobService {
  constructor(private dataService: DataService) {
  }

  getTestPackageJobs(): Observable<TestPackageJob[]> {
    let params = new HttpParams()
      .append('jobType', 'LOADER')
      .append('jobType', 'DELETE')
      .append('jobType', 'ROLLBACK');

    return this.dataService
      .get("/load", { params: params})
      .map(jobs => jobs.map(this.mapLoaderJobsFromApi));

    // return Observable.of(createMockLoaderJobs());
  }

  //TODO: Remove this mock method once we have a functional job system and real data
  private createMockLoaderJobs(): TestPackageJob[] {
    let error = new JobError();
    error.message = "This is an error";
    error.severity = "Error";
    error.system = 'TIS';

    let jobs: TestPackageJob[] = [];
    for (var i = 0; i < 30; i++) {
      let job = new TestPackageJob();
      job.id = i.toString();
      job.testPackageName = '(SBAC_PT)SBAC-ELA-G7-MATH' + i + '.xml';
      job.createdAt = new Date();
      job.type = "Create";

      if (i % 2 === 0) {
        job.tdsStepStatus = StepStatus.Fail;
        job.artStepStatus = StepStatus.Success;
        job.tisStepStatus = StepStatus.InProgress;
        job.thssStepStatus = StepStatus.InProgress;
        job.errors = [error];
      } else {
        job.tdsStepStatus = StepStatus.Success;
        job.artStepStatus = StepStatus.Success;
        job.tisStepStatus = StepStatus.Success;
        job.thssStepStatus = StepStatus.Success;
      }

      if (i === 7) {
        for (var j = 0; j < 20; j++) {
          let error2 = new JobError();
          error2.message = "This is a really really really long test delivery system error that should go beyond the width of the data grid. Should be fully displayable in a onhover tooltipe";
          error2.severity = "Error";
          error2.system = 'TDS';
          job.tdsStepStatus = StepStatus.Fail;
          job.artStepStatus = StepStatus.NotApplicable;
          job.tisStepStatus = StepStatus.NotApplicable;
          job.thssStepStatus = StepStatus.NotApplicable;
          job.errors.push(error2);
        }
      }

      jobs.push(job);
    }

    return jobs;
  }

  private mapLoaderJobsFromApi(apiModel): TestPackageJob {
    let job = new TestPackageJob();
    job.id = apiModel.id;

    job.testPackageName = apiModel.name;
    job.createdAt = new Date(apiModel.createdAt);
    job.type = apiModel.type;

    if (job.type === 'ROLLBACK') {
      job.parentJobId = apiModel.parentJobId;
    }

    job.tdsStepStatus = apiModel.steps
      .filter(step => step.name.indexOf('test-package-tds') >= 0)
      .map(step => step.status)[0] || StepStatus.NotApplicable;
    job.artStepStatus = apiModel.steps
      .filter(step => step.name.indexOf('test-package-art') >= 0)
      .map(step => step.status)[0] || StepStatus.NotApplicable;
    job.tisStepStatus = apiModel.steps
      .filter(step => step.name.indexOf('test-package-tis') >= 0)
      .map(step => step.status)[0] || StepStatus.NotApplicable;
    job.thssStepStatus = apiModel.steps
      .filter(step => step.name.indexOf('test-package-thss') >= 0)
      .map(step => step.status)[0] || StepStatus.NotApplicable;

    // Flatten all errors from each step into one array
    job.errors = [].concat.apply([], apiModel.steps.map(
      step => (step.errors || []).map(
        error => {
          let jobError = new JobError();

          switch (true) {
            case /test-package-tds/.test(step.name):
              jobError.system = 'TDS';
              break;
            case /test-package-art/.test(step.name):
              jobError.system = 'ART';
              break;
            case /test-package-tis/.test(step.name):
              jobError.system = 'TIS';
              break;
            case /test-package-thss/.test(step.name):
              jobError.system = 'THSS';
              break;
          }

          jobError.severity = error.severity;
          jobError.message = error.message;
          return jobError;
        }
      )
    ));

    return job;
  }
}
