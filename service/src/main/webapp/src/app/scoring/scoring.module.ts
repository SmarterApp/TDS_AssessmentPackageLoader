import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { NgxDatatableModule } from "@swimlane/ngx-datatable";
import { DataTableModule } from "primeng/primeng";
import {ScoringJobsComponent} from "./jobs/scoring-jobs.component";
import {ScoringJobDetailsComponent} from "./jobs/scoring-job-details.component";
import {ScoringComponent} from "./scoring.component";
import {ScoringJobsModule} from "./jobs/scoring-jobs.module";
import {UploadScoringJobModule} from "./jobs/upload/upload-scoring-job.module";

@NgModule({
  declarations: [
    ScoringComponent,
    ScoringJobsComponent,
    ScoringJobDetailsComponent
  ],
  imports: [
    ScoringJobsModule,
    BrowserModule,
    DataTableModule,
    NgxDatatableModule,
    FormsModule,
    UploadScoringJobModule
  ],
  exports: [
    ScoringComponent,
    ScoringJobsComponent
  ]
})
export class ScoringModule {
}
