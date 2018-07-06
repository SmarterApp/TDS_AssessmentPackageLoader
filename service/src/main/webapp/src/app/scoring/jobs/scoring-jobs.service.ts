import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../shared/data/data.service";
import 'rxjs/add/observable/of';
import {ScoringJob} from "./model/scoring-job.model";

@Injectable()
export class ScoringJobService {
  constructor(private dataService: DataService) {
  }

  getScoringJobs(): Observable<ScoringJob[]> {
    return this.dataService
      .get("/scoring")
      .map(jobs => jobs.map(this.mapScoringJobsFromApi));
  }

  private mapScoringJobsFromApi(apiModel): ScoringJob {
    let job = new ScoringJob();

    job.id = apiModel.id;
    job.examId = apiModel.examId;
    job.shortId = job.id.substr(job.id.length - 8);
    job.studentName = apiModel.studentName;
    job.assessmentId = apiModel.assessmentId;
    job.createdAt = new Date(apiModel.createdAt);
    job.status = apiModel.status;
    job.steps = apiModel.steps;
    return job;
  }
}
