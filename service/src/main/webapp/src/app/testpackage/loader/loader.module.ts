import {NgModule} from "@angular/core";
import {LoaderComponent} from "./loader.component";
import {LoaderJobsModule} from "./jobs/loader-jobs.module";
import {LoaderJobsComponent} from "./jobs/loader-jobs.component";
import {LoaderJobDetailsComponent} from "./jobs/loader-job-details.component";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {DataTableModule} from "primeng/primeng";
import {UploadLoaderJobModule} from "./jobs/upload/upload-loader-job.module";

@NgModule({
  declarations: [
    LoaderComponent,
    LoaderJobsComponent,
    LoaderJobDetailsComponent
  ],
  imports: [
    LoaderJobsModule,
    BrowserModule,
    DataTableModule,
    FormsModule,
    UploadLoaderJobModule
  ],
  exports: [
    LoaderComponent,
    LoaderJobsComponent
  ]
})
export class LoaderModule {
}
