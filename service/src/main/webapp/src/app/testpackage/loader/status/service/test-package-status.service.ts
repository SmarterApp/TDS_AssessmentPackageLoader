import {Injectable} from '@angular/core';
import {DataService} from "../../../../shared/data/data.service";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/mergeMap';
import {TestPackageStatusRowMapper} from "./test-package-status-mapper";
import {TestPackageStatusRow} from "../model/test-package-status-row";
import {HttpParams} from "@angular/common/http";


/**
 * A service for interacting with {TestPackageStatus} data.
 */
@Injectable()
export class TestPackageStatusService {

  constructor(private dataService: DataService) {
  }

  /**
   * Retrieve a list of {TestPackageStatus} records from the server.
   *
   * @return {List<TestPackageStatusRow>} representing the state * of each test package managed by the support tool.
   */
  getTestPackageStatusRows(): Observable<TestPackageStatusRow[]> {
    return this.dataService
      .get('/load/status', { params: new HttpParams() })
      .map(statuses => statuses.map(TestPackageStatusRowMapper.map));
  }

  /**
   * Delete a {TestPackage} from the downstream systems.
   *
   * @param {string} testPackageName The name of the {TestPackage} to delete
   */
  deleteTestPackage(testPackageName: string): void {
    this.dataService.delete('/load/' + testPackageName).subscribe();
  }
}
