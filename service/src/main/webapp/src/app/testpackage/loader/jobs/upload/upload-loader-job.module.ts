import {UploadLoaderJobComponent} from "./upload-loader-job.component";
import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FileUploadModule} from "ng2-file-upload";

@NgModule({
  declarations: [
    UploadLoaderJobComponent
  ],
  imports: [
    CommonModule,
    FileUploadModule
  ],
  exports: [
    UploadLoaderJobComponent
  ]
})
export class UploadLoaderJobModule {
}
