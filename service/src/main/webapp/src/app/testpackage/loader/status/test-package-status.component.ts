import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatusRow } from "./model/test-package-status-row";
import { StepStatus } from "../jobs/model/test-package-job.model";
import { TargetSystem } from "./model/target-system.enum";
import { SortDirection } from "../../../shared/data/sort-direction.enum";
import { PageResponse } from "../../../shared/data/page-response";
import 'rxjs/add/operator/debounceTime';
import "rxjs/add/operator/distinctUntilChanged";
import { PageRequest } from "../../../shared/data/page-request";
import { TimerObservable } from "rxjs/observable/TimerObservable";

/**
 * Controller for interacting with test package status data.
 *
 * NOTE:  Added encapsulation to @Component definition to get styling the row of the PrimeNG DataTable to work for
 * test packages that have been deleted.  Found this from "darklinki"'s answer on this thread:
 * https://github.com/angular/angular/issues/7845
 */
@Component({
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.css', '../../test-package.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TestPackageStatusComponent implements OnInit, OnDestroy {
  // Initialize the models to a default value
  private _testPackageStatusPage: PageResponse<TestPackageStatusRow> = {
    content: [],
    numberOfElements: 0,
    first: false,
    last: false,
    totalPages: 0,
    totalElements: 0,
    size: 0,
    number: 0,
    sort: []
  };

  private sortPreference = {
    sort: "uploadedAt",
    sortDir: SortDirection.Descending
  };

  private searchTermText = '';

  private isAlive: boolean = false; // used to unsubscribe from the TimerObservable when OnDestroy is called.

  constructor(private testPackageStatusService: TestPackageStatusService) {
    this.isAlive = false;
  }

  /**
   * @return {TestPackageStatusRow[]} A collection of {TestPackageStatusRow}s that represent the state of each test
   * package managed by the Support Tool.
   */
  get testPackageStatusPage(): PageResponse<TestPackageStatusRow> {
    return this._testPackageStatusPage;
  }

  set testPackageStatusPage(value: PageResponse<TestPackageStatusRow>) {
    this._testPackageStatusPage = value;
  }

  /**
   * Get the first page of {TestPackageStatusRow}s for display, sorted by "Last Uploaded At" in Descending order.
   */
  ngOnInit() {
    // Create an event to fetch the first page of records.
    const initPaginatorEvent = {
      page: 0,
      size: 2,
      sort: this.sortPreference.sort,
      sortDir: this.sortPreference.sortDir
    };

    this.getData(this.searchTermText, initPaginatorEvent);

    TimerObservable.create(5000, 5000)
      .takeWhile(() => this.isAlive)
      .subscribe(() => {
        this.getData(this.searchTermText, {
          page: this.testPackageStatusPage.number,
          size: this.testPackageStatusPage.size,
          sort: this.sortPreference.sort,
          sortDir: this.sortPreference.sortDir
        });
      });
  }

  /**
   * Unsubscribe from the timer when this component is destroyed.
   */
  ngOnDestroy() {
    this.isAlive = false;
  }

  /**
   * Get the next page of records.
   *
   * @param {event} event The event fired by the {Paginator#onPageChange} method
   */
  getNextPage(event) {
    const nextPage = {
      page: event.page,
      size: event.rows,
      sort: this.sortPreference.sort,
      sortDir: this.sortPreference.sortDir
    };

    this.getData(this.searchTermText, nextPage);
  }

  /**
   * Set the sorting preference and update the page.
   *
   * @param {event} event The sorting event from the {Paginator}'s sort method
   */
  setSortPreference(event) {
    this.sortPreference = {
      sort: event.field,
      sortDir: event.order == 1 ? SortDirection.Ascending : SortDirection.Descending
    };

    const pageRequest = {
      page: 0,
      size: this.testPackageStatusPage.size,
      sort: this.sortPreference.sort,
      sortDir: this.sortPreference.sortDir
    };

    this.getData(this.searchTermText, pageRequest);
  }

  /**
   * Search for {TestPackageStatus}es by name.
   *
   * @param {string} term Part of the name of the {TestPackageStatus}es to search for.
   */
  search(term: string) {
    const pageRequest = {
      page: this.testPackageStatusPage.number,
      size: this.testPackageStatusPage.size,
      sort: this.sortPreference.sort,
      sortDir: this.sortPreference.sortDir
    };

    this.getData(term, pageRequest);
  }

  /**
   * Fetch a page of {TestPackageStatusRow}s from the server based on the paging, sorting and search criteria.
   *
   * @param {string} searchTerm The part of the test name being
   * @param {PageRequest} page information for the request
   */
  private getData(searchTerm: string, page: PageRequest) {
    if (searchTerm !== '') {
      this.testPackageStatusService.searchByName(searchTerm, page)
        .subscribe(response => this.testPackageStatusPage = response);
    } else {
      this.testPackageStatusService.getAll(page)
        .subscribe(response => this.testPackageStatusPage = response);
    }
  }

  /**
   * Get the appropriate CSS class for the specified {StepStatus}
   * <p>
   *     This method cannot be static; Angular views cannot access static methods
   * </p>
   *
   * @param {StepStatus} status The {StepStatus} to evaluate
   * @return {string} The CSS class that represents the specified {StepStatus}
   */
  getStatusIconCss(status: StepStatus): string {
    return TestPackageStatusRow.getStatusIconClass(status);
  }

  /**
   * Build a tooltip description message for the {TargetSystem}'s {StepStatus}
   * <p>
   *     This method cannot be static; Angular views cannot access static methods
   * </p>
   *
   * @param {StepStatus} status The target system's {StepStatus}
   * @param {TargetSystem} system The {TargetSystem}
   * @return {string} A message describing what the status icon means
   */
  getStatusDescription(status: StepStatus, system: TargetSystem): string {
    let statusMessage = `The test package was not loaded into ${ system }`;

    switch (status) {
      case StepStatus.Success:
        statusMessage = `The test package was loaded into ${ system } successfully`;
        break;
      case StepStatus.Fail:
        statusMessage = `The test package could not be loaded into ${ system }`;
        break;
      default:
        break;
    }

    return statusMessage;
  }

  getRowCss(rowData: TestPackageStatusRow) {
    return rowData.jobType == 'DELETE'
    ? 'status-is-deleted'
      : ''
  }

  /**
   * Delete a test package from all the systems it has been loaded into.
   *
   * @param {string} name The name of the test package to delete
   */
  deleteTestPackage(name: string) {
    const message = `Are you sure you want to delete the '${ name }' test package?`;
    if (window.confirm(message)) {
      this.testPackageStatusService.deleteTestPackage(name);

      alert("Test Package '" + name + "' is in the process of being deleted.  Once the test package has been deleted from all systems, the test packag willl no longer appear in this list.");

      this.getData(this.searchTermText, {
        page: 0,
        size: this.testPackageStatusPage.size,
        sort: this.sortPreference.sort,
        sortDir: this.sortPreference.sortDir
      });
    }
  }
}
