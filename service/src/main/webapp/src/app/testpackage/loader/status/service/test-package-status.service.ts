import { Injectable } from '@angular/core';
import { DataService } from "../../../../shared/data/data.service";
import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/mergeMap';
import { TestPackageStatusRowMapper } from "./test-package-status-mapper";
import { TestPackageStatusRow } from "../model/test-package-status-row";


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
   * @return {TestPackageStatusRow[]} a collection of {TestPackageStatusRow}s representing the state of each test
   * package managed by the support tool.
   */
  getAll(): Observable<TestPackageStatusRow[]> {
    return this.dataService.get('/load/status', {observe: 'response'})
      .map(response => {
        const statuses = response.body;
        return statuses.map(statusJson => TestPackageStatusRowMapper.map(statusJson))
      });
  }
}
