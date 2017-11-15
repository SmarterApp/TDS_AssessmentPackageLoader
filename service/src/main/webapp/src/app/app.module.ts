import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FileUploadModule } from "ng2-file-upload";

import { AppComponent } from './app.component';
import { TestPackageUploadComponent } from "./testpackage/testpackage-upload.component/testpackage-upload.component";

@NgModule({
  declarations: [
    AppComponent,
    TestPackageUploadComponent
  ],
  imports: [
    BrowserModule,
    FileUploadModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
