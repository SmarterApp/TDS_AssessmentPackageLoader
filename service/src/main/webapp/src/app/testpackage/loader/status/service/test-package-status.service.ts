import { Injectable } from '@angular/core';
import { TestPackageStatus } from "../model/test-package-status";
import { DataService } from "../../../../shared/data/data.service";
import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/mergeMap';
import { TestPackageStatusMapper } from "./test-package-status-mapper";


/**
 * A service for interacting with {TestPackageStatus} data.
 */
@Injectable()
export class TestPackageStatusService {

  constructor(private dataService: DataService) {
  }

  /**
   * Retrieve all the {TestPackageStatus} records from the server.
   *
   * @return {TestPackageStatus[]} a collection of {TestPackageStatus} records
   */
  getAll(): Observable<TestPackageStatus[]> {
    return this.dataService.get('/load/status', { observe : 'response' })
      .map(response => {
        const statuses = response.body;
        return statuses.map(statusJson => TestPackageStatusMapper.map(statusJson))
      });
  }
}
