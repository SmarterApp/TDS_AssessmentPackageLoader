import {NgModule} from "@angular/core";
import {TestPackageComponent} from "./test-package.component";
import {TestPackageJobsModule} from "./jobs/test-package-jobs.module";
import {TestPackageJobsComponent} from "./jobs/test-package-jobs.component";
import {TestPackageJobDetailsComponent} from "./jobs/test-package-job-details.component";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {DataTableModule} from "primeng/primeng";
import {UploadLoaderJobModule} from "./jobs/upload/upload-loader-job.module";
import { TestPackageStatusComponent } from './status/test-package-status.component';

@NgModule({
  declarations: [
    TestPackageComponent,
    TestPackageJobsComponent,
    TestPackageJobDetailsComponent,
    TestPackageStatusComponent
  ],
  imports: [
    TestPackageJobsModule,
    BrowserModule,
    DataTableModule,
    FormsModule,
    UploadLoaderJobModule
  ],
  exports: [
    TestPackageComponent,
    TestPackageJobsComponent
  ]
})
export class TestPackageModule {
}
