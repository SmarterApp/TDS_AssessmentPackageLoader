import { TestBed, inject } from '@angular/core/testing';

import { TestPackageStatusService } from './test-package-status.service';

describe('TestPackageStatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TestPackageStatusService]
    });
  });

  it('should be created', inject([TestPackageStatusService], (service: TestPackageStatusService) => {
    expect(service).toBeTruthy();
  }));
});
