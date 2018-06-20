import {UploadScoringJobComponent} from "./upload-scoring-job.component";
import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FileUploadModule} from "ng2-file-upload";
import {DataTableModule} from "primeng/primeng";

@NgModule({
  declarations: [
    UploadScoringJobComponent
  ],
  imports: [
    CommonModule,
    FileUploadModule,
    DataTableModule
  ],
  exports: [
    UploadScoringJobComponent
  ]
})
export class UploadScoringJobModule {
}
