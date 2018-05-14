import { TestBed, inject } from '@angular/core/testing';

import { TestPackageStatusService } from './test-package-status.service';
import { TestPackageStatusRowMapper } from "./test-package-status-mapper";

describe('TestPackageStatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TestPackageStatusService]
    });
  });

  it('should be created', inject([TestPackageStatusService], (service: TestPackageStatusService) => {
    expect(service).toBeTruthy();
  }));

  it('should map a test package status JSON to TestPackageStatus', () => {
    let mockApiResult = {
      "name": "test package: all systems, some errors",
      "uploadedAt": new Date("2018-02-06T12:37:54.056"),
      "jobId": "abc123",
      "targets": [{
        "target": "TDS",
        "status": "SUCCESS",
        "statusDate": "2018-02-06T12:37:54.056"
      }, {
        "target": "ART",
        "status": "SUCCESS",
        "statusDate": "2018-02-06T12:37:54.056"
      }, {
        "target": "TIS",
        "status": "FAIL",
        "statusDate": "2018-02-06T12:37:54.056"
      }, {
        "target": "THSS",
        "status": "FAIL",
        "statusDate": "2018-02-06T12:37:54.056"
      }]
    };

    let status = TestPackageStatusRowMapper.map(mockApiResult);

    expect(status.name).toBe("test package: all systems, some errors");
    expect(status.tdsStatus).toBe("SUCCESS");
    expect(status.artStatus).toBe("SUCCESS");
    expect(status.tisStatus).toBe("FAIL");
    expect(status.thssStatus).toBe("FAIL");
  });
});
