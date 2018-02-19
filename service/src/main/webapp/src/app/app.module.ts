import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {RouterModule} from "@angular/router";
import {routes} from "./app.routes";
import {TestPackageModule} from "./testpackage/loader/test-package.module";
import {BreadcrumbsComponent} from "./breadcrumbs/breadcrumbs.component";
import {CommonModule} from "./shared/common.module";
import { TestPackageStatusModule } from "./testpackage/loader/status/test-package-status.module";

@NgModule({
  declarations: [
    AppComponent,
    BreadcrumbsComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    TestPackageModule,
    TestPackageStatusModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
