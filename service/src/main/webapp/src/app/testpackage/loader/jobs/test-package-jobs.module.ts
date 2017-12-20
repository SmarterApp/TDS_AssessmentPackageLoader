import {NgModule} from "@angular/core";
import {DataTableModule} from "primeng/primeng";
import {TestPackageJobService} from "./test-package-jobs.service";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    BrowserModule,
    DataTableModule,
    FormsModule
  ],
  providers: [
    TestPackageJobService
  ]
})
export class TestPackageJobsModule {
}
