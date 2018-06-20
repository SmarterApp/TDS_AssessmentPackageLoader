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
    // TODO: Fetch data from server once back end system is in place
    // return this.dataService
    //   .get("/scoring")
    //   .map(jobs => jobs.map(this.mapScoringJobsFromApi));

    return Observable.of(ScoringJobService.createMockScoringJobs());
  }

  private static createMockScoringJobs(): ScoringJob[] {
    return [{
      id: '6f9c1d02-d919-4497-b2c8-3a47b2abb83d',
      examId: '8bc01931-cbb1-4eea-8de1-b7743a38b82f',
      shortId: '3a47b2abb83d',
      studentName: 'Ernest Munoz-Diaz',
      assessmentId: 'SBAC-IAB-FIXED-G5E-BriefWrites-ELA-5',
      createdAt: new Date(),
      status: 'FAILED',
      steps: [{
        status: 'FAILED',
        system: 'TIS',
        message: 'TIS could not find a configuration for the test \"SBAC-IAB-FIXED-G5E-BriefWrites-ELA-5\".',
        updatedAt: new Date()
      }]
    }, {
      id: '7a426024-1121-4e67-a9a7-5e90a18dc6f2',
      examId: '30f64038-622d-4c74-9c43-3fc4adfe59e3',
      shortId: '5e90a18dc6f2',
      studentName: 'Jeff Johnson',
      assessmentId: 'SBAC-IAB-FIXED-G5E-BriefWrites-ELA-7',
      createdAt: new Date(),
      status: 'FINISHED',
      steps: [{
        status: 'SUCCESS',
        system: 'Test Integration System',
        message: 'TIS has successfully rescored the TRT.',
        updatedAt: new Date()
      }, {
        status: 'SUCCESS',
        system: 'Exam Results Transmitter',
        message: 'ERT successfully retrieved the TDS re-scored TRT',
        updatedAt: new Date()
      }]
    }];
  }

  private mapScoringJobsFromApi(apiModel): ScoringJob {
    let job = new ScoringJob();

    return job;
  }
}
