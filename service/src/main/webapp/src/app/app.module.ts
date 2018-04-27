import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {RouterModule} from "@angular/router";
import {routes} from "./app.routes";
import { BsDropdownModule} from "ngx-bootstrap";
import {TestPackageModule} from "./testpackage/loader/test-package.module";
import {BreadcrumbsComponent} from "./breadcrumbs/breadcrumbs.component";
import {CommonModule} from "./shared/common.module";
import { TestPackageStatusModule } from "./testpackage/loader/status/test-package-status.module";
import {UserModule} from "./user/user.module";
import {ErrorComponent} from "./error.component";

@NgModule({
  declarations: [
    AppComponent,
    BreadcrumbsComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    TestPackageModule,
    TestPackageStatusModule,
    UserModule,
    BsDropdownModule.forRoot(),
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
