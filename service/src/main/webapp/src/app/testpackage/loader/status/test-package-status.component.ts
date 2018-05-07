import {Component, OnDestroy, OnInit, TemplateRef, ViewChild, ViewEncapsulation} from '@angular/core';
import {TestPackageStatusService} from "./service/test-package-status.service";
import {TestPackageStatusRow} from "./model/test-package-status-row";
import {StepStatus} from "../jobs/model/test-package-job.model";
import {TargetSystem} from "./model/target-system.enum";
import 'rxjs/add/operator/debounceTime';
import "rxjs/add/operator/distinctUntilChanged";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Router} from "@angular/router";

/**
 * Controller for interacting with test package status data.
 *
 * NOTE:  Added encapsulation to @Component definition to get styling the row of the PrimeNG DataTable to work for
 * test packages that have been deleted.  Found this from "darklinki"'s answer on this thread:
 * https://github.com/angular/angular/issues/7845
 */
@Component({
  selector: 'test-package-status',
  templateUrl: './test-package-status.component.html',
  styleUrls: ['./test-package-status.component.scss', '../../test-package.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TestPackageStatusComponent implements OnInit, OnDestroy {

  @ViewChild('deleteTmpl') deleteTmpl: TemplateRef<any>;
  @ViewChild('dateTmpl') dateTmpl: TemplateRef<any>;
  @ViewChild('systemTmpl') systemTmpl: TemplateRef<any>;

  StepStatuses = StepStatus; // Need to include the enum as a property to access it in template
  searchTerm: string = '';
  private alive: boolean; // used to unsubscribe from the TimerObservable when OnDestroy is called.

  filteredPackageStatuses: TestPackageStatusRow[];
  private _testPackageStatuses: TestPackageStatusRow[];
  columns = [];

  constructor(private testPackageStatusService: TestPackageStatusService,
              private router: Router) {
    this.alive = true;
  }

  /**
   * @return {TestPackageStatusRow[]} A collection of {TestPackageStatusRow}s that represent the state of each test
   * package managed by the Support Tool.
   */
  get testPackageStatuses(): TestPackageStatusRow[] {
    console.log("returning statuses", this._testPackageStatuses);
    return this._testPackageStatuses;
  }

  set testPackageStatuses(value: TestPackageStatusRow[]) {
    this._testPackageStatuses = value;
    this.updateFilteredTestPackageStatuses();
  }

  /**
   * Get the first page of {TestPackageStatusRow}s for display, sorted by "Last Uploaded At" in Descending order.
   */
  ngOnInit() {
    this.columns = [
      {prop: 'name', name: 'Test Package Name', width: 480},
      {prop: 'uploadedAt', name: 'Last Uploaded At', cellTemplate: this.dateTmpl, width: 100},
      {prop: 'tdsStatus', name: 'TDS', headerClass: "text-align-center", cellTemplate: this.systemTmpl, width: 20},
      {prop: 'artStatus', name: 'ART', headerClass: "text-align-center", cellTemplate: this.systemTmpl, width: 20},
      {prop: 'tisStatus', name: 'TIS', headerClass: "text-align-center", cellTemplate: this.systemTmpl, width: 20},
      {prop: 'thssStatus', name: 'THSS', headerClass: "text-align-center", cellTemplate: this.systemTmpl, width: 20},
      {name: 'Delete', headerClass: "text-align-center", cellTemplate: this.deleteTmpl, width: 20},
    ];

    TimerObservable.create(0, 5000)
      .takeWhile(() => this.alive)
      .subscribe(() => {
        this.updateResults();
      });
  }

  ngOnDestroy() {
    this.alive = false;
  }

  updateResults() {
    this.testPackageStatusService
      .getTestPackageStatusRows()
      .subscribe(rows => {
        this.testPackageStatuses = rows;
      });
  }

  onSearchChange() {
    this.updateFilteredTestPackageStatuses();
  }

  updateFilteredTestPackageStatuses() {
    this.filteredPackageStatuses = this._testPackageStatuses
      .filter(x => x.name.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0
        || x.jobId.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0);
  }

  /**
   * Navigate to the upload screen to upload a test package file
   */
  uploadClick() {
    this.router.navigateByUrl('/loader/upload');
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

  /**
   * Apply a CSS class to indicate a row represents a {TestPackage} that is in the process of being deleted
   *
   * @param {TestPackageStatusRow} rowData The row to evaluate
   * @return {string | string} The name of the CSS class, or an empty string if no CSS needs to be added
   */
  rowClass = (rowData: TestPackageStatusRow) => {
    return rowData.jobType == 'DELETE'
      ? 'status-is-deleted'
      : ''
  };

  /**
   * Delete a test package from all the systems it has been loaded into.
   *
   * @param {string} name The name of the test package to delete
   */
  deleteTestPackage(name: string) {
    const message = `Are you sure you want to delete the '${ name }' test package? The test package will be deleted from all systems it is currently loaded into.`;
    if (window.confirm(message)) {
      this.testPackageStatusService.deleteTestPackage(name);

      alert("Test Package '" + name + "' is in the process of being deleted.  Once the test package has been deleted from all systems, the test package will no longer appear in this list.");

      this.updateResults();
    }
  }
}
