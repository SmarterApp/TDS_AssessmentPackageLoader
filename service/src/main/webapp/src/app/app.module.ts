import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FileUploadModule } from "ng2-file-upload";

import { AppComponent } from './app.component';
import { TestPackageUploadComponent } from "./testpackage/shared/testpackage-upload.component/testpackage-upload.component";
import { RouterModule } from "@angular/router";
import { routes } from "./app.routes";
import {LoaderModule} from "./testpackage/loader/loader.module";
import {BreadcrumbsComponent} from "./breadcrumbs/breadcrumbs.component";
import {CommonModule} from "./shared/common.module";

@NgModule({
  declarations: [
    AppComponent,
    BreadcrumbsComponent,
    TestPackageUploadComponent
  ],
  imports: [
    BrowserModule,
    FileUploadModule,
    CommonModule,
    LoaderModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
