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
      .append('jobType', 'LOAD')
      .append('jobType', 'DELETE')
      .append('jobType', 'ROLLBACK');

    return this.dataService
      .get("/load", { params: params})
      .map(jobs => jobs.map(this.mapLoaderJobsFromApi));

    // return Observable.of(createMockLoaderJobs());
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

    job.validationStepStatus = apiModel.steps
      .filter(step => step.name.indexOf('test-package-validate') >= 0)
      .map(step => step.status)[0] || StepStatus.NotApplicable;
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
            case /test-package-file-upload/.test(step.name):
              jobError.system = 'Validation';
              break;
            case /test-package-validate/.test(step.name):
              jobError.system = 'Validation';
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
