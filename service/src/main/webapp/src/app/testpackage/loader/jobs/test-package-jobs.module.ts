import {NgModule} from "@angular/core";
import {TestPackageJobService} from "./test-package-jobs.service";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [
    TestPackageJobService
  ]
})
export class TestPackageJobsModule {
}
