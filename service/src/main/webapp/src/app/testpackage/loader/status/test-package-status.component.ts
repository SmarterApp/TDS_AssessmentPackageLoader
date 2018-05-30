import {Component, OnDestroy, OnInit, TemplateRef, ViewChild, ViewEncapsulation} from '@angular/core';
import {TestPackageStatusService} from "./service/test-package-status.service";
import {TestPackageStatusRow} from "./model/test-package-status-row";
import {StepStatus} from "../jobs/model/test-package-job.model";
import 'rxjs/add/operator/debounceTime';
import "rxjs/add/operator/distinctUntilChanged";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Router} from "@angular/router";
import {AuthGuard} from "../../../auth.component";

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
              private router: Router,
              private authGuard: AuthGuard) {
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
        },
        error => {
          console.log("test-package-status.component got error getting data - refreshing auth");
          this.authGuard.updateUser();
        });
  }

  onSearchChange() {
    this.updateFilteredTestPackageStatuses();
  }

  updateFilteredTestPackageStatuses() {
    this.filteredPackageStatuses = this._testPackageStatuses
      .filter(x => x.name.toUpperCase().indexOf(this.searchTerm.toUpperCase()) >= 0);
  }

  /**
   * Navigate to the upload screen to upload a test package file
   */
  uploadClick() {
    this.router.navigateByUrl('/loader/upload');
  }

  /**
   * Build a tooltip description message for the {StepStatus}
   *
   * @param {TestPackageStatusRow} row The row to evaluate.
   * @param {string} value The StepStatus string.
   * @return {string} A message describing what the status icon means
   */
  getStatusDescription(row: TestPackageStatusRow, value: string): string {
    if(value == StepStatus.Success) {
      return `The test package has been loaded into this system.`;
    }
    return `The test package has not been loaded into this system.`;
  }
  /**
   * Apply a CSS class to provide the image for the row/system status cell.
   *
   * @param {TestPackageStatusRow} row The row to evaluate.
   * @param {string} value The StepStatus string.
   * @return {string | string} A string space-separated CSS classes to provide the icon.
   */
  getStatusClass(row: TestPackageStatusRow, value: string): string {
    if(value == StepStatus.Success) {
      return "fa fa-check-circle load-success";
    }
    return "fa fa-minus load-not-applicable";
  }


  /**
   * Delete a test package from all the systems it has been loaded into.
   *
   * @param {string} name The name of the test package to delete
   */
  deleteTestPackage(name: string) {
    const message = `Are you sure you want to delete the '${ name }' test package? The test package will be deleted from all systems it is currently loaded into.`;
    if (window.confirm(message)) {
      this.testPackageStatusService.deleteTestPackage(name);
      this.updateResults();
    }
  }
}
