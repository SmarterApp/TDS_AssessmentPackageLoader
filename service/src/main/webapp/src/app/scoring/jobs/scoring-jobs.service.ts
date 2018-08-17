import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../shared/data/data.service";
import 'rxjs/add/observable/of';
import {ScoringJob} from "./model/scoring-job.model";
import {ScoringValidationReport} from "./model/scoring-validation-report.model";

@Injectable()
export class ScoringJobService {
  constructor(private dataService: DataService) {
  }

  getScoringJobs(): Observable<ScoringJob[]> {
    return this.dataService
      .get("/scoring")
      .map(jobs => jobs.map(this.mapScoringJobsFromApi));
    // return this.mockCompletelJob();
  }

  getOriginalTrt(jobId: string): Observable<string> {
    return this.dataService
      .get(`/scoring/${jobId}/original`, { responseType: 'text'});
  }

  getRescoredTrt(jobId: string): Observable<string> {
    return this.dataService
      .get(`/scoring/${jobId}/rescored`, { responseType: 'text'});
  }

  getScoringValidationReportRaw(jobId: string): Observable<string> {
    return this.dataService
      .get(`/scoring/${jobId}/report`, { responseType: 'text'});
  }

  getScoringValidationReport(jobId: string): Observable<ScoringValidationReport> {
    return this.dataService
      .get(`/scoring/${jobId}/report`);
  }

  private mockScoringValidationReport(): Observable<any> {
    let report = {
      "jobId": "5b5679ce35f6ce5667346c53",
      "differenceReport": {
        "opportunity": {
          "scores": [{
            "status": "removed",
            "identifier": {
              "measureLabel": "ScaleScore",
              "measureOf": "Overall"
            },
            "value": {
              "from": "245.174914080214"
            },
            "standardError": {
              "from": "19.3617008392283"
            }
          }, {
            "status": "added",
            "identifier": {
              "measureLabel": "ScaleScore",
              "measureOf": "OverallXYZ"
            },
            "value": {
              "to": "245.174914080214"
            },
            "standardError": {
              "to": "19.3617008392283"
            }
          }, {
            "status": "modified",
            "identifier": {
              "measureLabel": "PerformanceLevel",
              "measureOf": "Reading"
            },
            "value": {
              "from": "2",
              "to": "25000"
            }
          }],
          "items": [{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          },{
            "status": "removed",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 15558
            },
            "score": {
              "from": "0"
            },
            "scoreStatus": {
              "from": "SCORED"
            }
          }, {
            "status": "added",
            "identifier": {
              "position": 1,
              "bankKey": 200,
              "key": 99999
            },
            "score": {
              "to": "0"
            },
            "scoreStatus": {
              "to": "SCORED"
            }
          }, {
            "status": "modified",
            "identifier": {
              "position": 5,
              "bankKey": 200,
              "key": 15560
            },
            "score": {
              "from": "-1",
              "to": "10"
            },
            "scoreStatus": {
              "from": "NOTSCORED",
              "to": "SCORED"
            }
          }]
        }
      }
    };
    return Observable.of(report);
  }

  private mockCompletelJob(): Observable<any[]> {
    return Observable.of([
      {
        id:"5b52377702f0ddac958456df",
        shortId: '958456df',
        name:"test-trt",
        createdAt: new Date("2018-07-20T12:26:47.597"),
        type:"SCORING",
        steps:[
          {
            description:"Uploading test results transmission file",
            status:"SUCCESS",
            name:"test-results-file-upload",
            complete:true,
            createdAt:"2018-07-20T12:25:47.597",
            jobStepTarget:"Internal"
          },{
            description:"Sending the test results to the Exam Service to re-score",
            status:"SUCCESS",
            name:"test-results-rescore",
            complete:true,
            createdAt:"2018-07-20T12:26:47.597",
            jobStepTarget:"TDS"
          },{
            description:"Exam Results Transmitter has received the re-scored TRT from the Exam Service",
            status:"SUCCESS",
            name:"exam-results-receive",
            complete:true,
            createdAt:"2018-07-20T12:27:42.597",
            jobStepTarget:"ERT"
          },{
            description:"Exam Results Transmitter has successfully sent the TRT to TIS for exam-level rescoring",
            status:"SUCCESS",
            name:"exam-results-send",
            complete:true,
            createdAt:"2018-07-20T12:27:44.597",
            jobStepTarget:"ERT"
          },{
            description:"TIS has processed and re-scored the TRT",
            status:"SUCCESS",
            name:"exam-results-tis-rescore",
            complete:true,
            createdAt:"2018-07-20T12:29:51.597",
            jobStepTarget:"TIS"
          }
        ],
        userName:"ca.admin@example.com",
        examId:"4a3b714d-fcf9-42ab-a072-9b225494cdba",
        assessmentId:"SBAC-MATH-5",
        studentName:"HAPPY, JAMES5",
        status:"SUCCESS",
        complete: true,
        originalTrtSaved: true
      }]);
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

    // If the job has received a successful status update from TIS, then it is considered complete
    for (let i = 0; i < job.steps.length; i++) {
      if (job.steps[i].jobStepTarget === 'Internal' && job.steps[i].status === 'SUCCESS') {
        job.originalTrtSaved = true;
      }
    }

    if (job.status === 'SUCCESS') {
      console.log("Setting job.complete to true");
      job.complete = true;
    }

    console.log("Value of job.complete = " + job.complete);
    return job;
  }
}
