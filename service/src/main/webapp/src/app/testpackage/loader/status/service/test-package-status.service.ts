import { Injectable } from '@angular/core';
import { DataService } from "../../../../shared/data/data.service";
import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/mergeMap';
import { TestPackageStatusRowMapper } from "./test-package-status-mapper";
import { TestPackageStatusRow } from "../model/test-package-status-row";
import { HttpParams } from "@angular/common/http";
import { PageRequest } from "../../../../shared/data/page-request";
import { PageResponse } from "../../../../shared/data/page-response";


/**
 * A service for interacting with {TestPackageStatus} data.
 */
@Injectable()
export class TestPackageStatusService {

  constructor(private dataService: DataService) {
  }

  /**
   * Retrieve a page of {TestPackageStatus} records from the server.
   *
   * @return {Page<TestPackageStatusRow>} that includes a collection of {TestPackageStatusRow}s representing the state
   * of each test package managed by the support tool.  Additionally, paging information is returned to allow users to
   * navigate between pages of records.
   */
  getAll(page: PageRequest): Observable<PageResponse<TestPackageStatusRow>> {
    const params = new HttpParams()
      .append('page', (page.page || 0).toLocaleString())
      .append('size', (page.size || 10).toLocaleString())
      .append('sort', (page.sort + "," + page.sortDir || 'uploadedAt,DESC'));

    return this.dataService.get('/load/status', { observe: 'response', params: params })
      .map(response => {
        let pageData = response.body;
        pageData.content = pageData.content.map(statusJson => TestPackageStatusRowMapper.map(statusJson));

        return pageData;
      });
  }

  searchByName(searchTerm: string, page: PageRequest): Observable<PageResponse<TestPackageStatusRow>> {
    const params = new HttpParams()
      .append('page', (page.page || 0).toLocaleString())
      .append('size', (page.size || 10).toLocaleString())
      .append('sort', (page.sort + "," + page.sortDir || 'uploadedAt,DESC'))
      .append('searchTerm', searchTerm);

    return this.dataService.get('/load/status/search', { observe: 'response', params: params })
      .map(response => {
        // When there are no results, the content array will not be present
        let pageData = response.body.content || [];
        pageData = pageData.map(statusJson => TestPackageStatusRowMapper.map(statusJson));

        return pageData;
      });
  }
}
