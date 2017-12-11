import { LoaderJobService } from "./loader-jobs.service";
import {Observable} from "rxjs/Observable";
import {DataService} from "../../../shared/data/data.service";


let loaderJobsObserver;
let optionsSpy;

describe("Loader Jobs Service", () => {
  let service;

  beforeEach(() => {
    service = new LoaderJobService(new MockDataService());
  });

  it('Should return an empty array for no test packages found', () => {
    // No ART step - other systems in pending/not-started state
    let mockApiResult = [];

    service.getLoaderJobs().subscribe(actual => {
      expect(actual.length).toBe(0);
    });

    loaderJobsObserver.next(mockApiResult);

    let actualParams = optionsSpy.params.updates[0];
    expect(actualParams["param"]).toEqual("jobType");
    expect(actualParams["value"]).toEqual("LOADER");
  });

  it('Should map loader job data for job with no errors', () => {
    // No ART step - other systems in pending/not-started state
    let mockApiResult = [{
      createdAt: "2017-12-06T13:56:07.917",
      id: "5a286777d66e2a30d10eef97",
      status: "IN_PROGRESS",
      steps: [
        {
          description: "Uploading test package",
          errors: [],
          name: "test-package-file-upload",
          status: "SUCCESS"
        },
        {
          description: "Parsing and validating test package",
          errors: [],
          name: "test-package-validate",
          status: "NOT_STARTED"
        },
        {
          description: "Uploading test package to Student and Proctor",
          errors: [],
          name: "test-package-tds-upload",
          status: "NOT_STARTED"
        },
        {
          description: "Uploading test package to TIS",
          errors: [],
          name: "test-package-tis-upload",
          status: "NOT_STARTED"
        },
        {
          description: "Uploading test package to THSS",
          errors: [],
          name: "test-package-thss-upload",
          status: "NOT_STARTED"
        }
      ],
      testPackageFileName: "(SBAC_PT)IRP-Perf-ELA-7-Summer-2015-2016.xml",
      type: "LOADER"
    }];

    service.getLoaderJobs().subscribe(actual => {
      expect(actual.length).toBe(1);
      expect(actual[0].id).toBe(mockApiResult[0].id);
      expect(actual[0].createdAt.toLocaleString()).toBe(new Date(mockApiResult[0].createdAt).toLocaleString());
      expect(actual[0].testPackageName).toBe(mockApiResult[0].testPackageFileName);
      expect(actual[0].tdsStepStatus).toBe('NOT_STARTED');
      expect(actual[0].artStepStatus).toBe('NOT_APPLICABLE');
      expect(actual[0].tisStepStatus).toBe('NOT_STARTED');
      expect(actual[0].thssStepStatus).toBe('NOT_STARTED');
      expect(actual[0].errors.length).toBe(0);
    });

    loaderJobsObserver.next(mockApiResult);

    let actualParams = optionsSpy.params.updates[0];
    expect(actualParams["param"]).toEqual("jobType");
    expect(actualParams["value"]).toEqual("LOADER");
  });

  it('Should map loader job data for job with errors', () => {
    // No ART step - other systems in pending/not-started state
    let mockApiResult = [{
      createdAt: "2017-12-06T13:56:07.917",
      id: "5a286777d66e2a30d10eef97",
      status: "IN_PROGRESS",
      steps: [
        {
          description: "Uploading test package",
          errors: [],
          name: "test-package-file-upload",
          status: "SUCCESS"
        },
        {
          description: "Parsing and validating test package",
          errors: [],
          name: "test-package-validate",
          status: "SUCCESS"
        },
        {
          description: "Uploading test package to Student and Proctor",
          errors: [],
          name: "test-package-tds-upload",
          status: "NOT_STARTED"
        },
        {
          description: "Uploading test package to TIS",
          errors: [
            {
              severity: 'CRITICAL',
              message: 'This is an error'
            }
          ],
          name: "test-package-tis-upload",
          status: "FAILED"
        },
        {
          description: "Uploading test package to THSS",
          errors: [
            {
              severity: 'CRITICAL',
              message: 'This is an error 1'
            },
            {
              severity: 'CRITICAL',
              message: 'This is an error 2'
            }
          ],
          name: "test-package-thss-upload",
          status: "FAILED"
        }
      ],
      testPackageFileName: "(SBAC_PT)IRP-Perf-ELA-7-Summer-2015-2016.xml",
      type: "LOADER"
    }];

    service.getLoaderJobs().subscribe(actual => {
      expect(actual.length).toBe(1);
      expect(actual[0].id).toBe(mockApiResult[0].id);
      expect(actual[0].createdAt.toLocaleString()).toBe(new Date(mockApiResult[0].createdAt).toLocaleString());
      expect(actual[0].testPackageName).toBe(mockApiResult[0].testPackageFileName);
      expect(actual[0].tdsStepStatus).toBe('NOT_STARTED');
      expect(actual[0].artStepStatus).toBe('NOT_APPLICABLE');
      expect(actual[0].tisStepStatus).toBe('FAILED');
      expect(actual[0].thssStepStatus).toBe('FAILED');
      expect(actual[0].errors.length).toBe(3);
      expect(actual[0].errors[0].severity).toBe('CRITICAL');
      expect(actual[0].errors[0].message).toBe('This is an error');
    });

    loaderJobsObserver.next(mockApiResult);

    let actualParams = optionsSpy.params.updates[0];
    expect(actualParams["param"]).toEqual("jobType");
    expect(actualParams["value"]).toEqual("LOADER");
  });

});

class MockDataService extends DataService {
  get(route, options): Observable<any> {
    optionsSpy = options;

    if (route === "/load")
      return new Observable(o => loaderJobsObserver = o);

    throw Error("Unexpected route called");
  }
}
