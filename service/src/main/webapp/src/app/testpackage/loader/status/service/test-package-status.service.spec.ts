import { TestBed, inject } from '@angular/core/testing';

import { TestPackageStatusService } from './test-package-status.service';
import { TestPackageStatusMapper } from "./test-package-status-mapper";

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
      "uploadedAt": "2018-02-06T12:37:54.056",
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

    let status = TestPackageStatusMapper.map(mockApiResult);

    expect(status.name).toBe("test package: all systems, some errors");
    expect(status.targets.length).toBe(4);
  });
});
