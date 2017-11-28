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
    // let params: URLSearchParams = this.mapQueryToParams(query);

    // return this.dataService
    //   .get("/loader-Jobs", { search: params })
    //   .map(groups => groups.map(this.mapGroupFromApi));
    let error = new JobError();
    error.message = "This is an error";
    error.severity = "Error";
    error.system = 'TIS';

    let jobs: LoaderJob[] = [];
    for (var i = 0; i < 30; i++) {
      let job = new LoaderJob();
      job.id = i;
      job.testPackageName = 'testPackage' + i;
      job.dateCreated = new Date().toString();
      job.type = "Create";


      job.errors = i % 3 ? [] : [error];
      jobs.push(job);
    }

    return Observable.of(jobs);
  }
}
