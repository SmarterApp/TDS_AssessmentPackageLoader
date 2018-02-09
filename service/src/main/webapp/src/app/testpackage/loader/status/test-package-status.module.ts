import { NgModule } from '@angular/core';
import {DataTableModule} from "primeng/primeng";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatusComponent } from "./test-package-status.component";
import { TestPackageStatusMapper } from "./service/test-package-status-mapper";

@NgModule({
  declarations: [
    TestPackageStatusComponent
  ],
  // TODO:  Do I need to do this?  They're already imported in the TestPackageModule
  imports: [
    BrowserModule,
    DataTableModule,
    FormsModule
  ],
  providers: [
    TestPackageStatusService
  ]
})
export class TestPackageStatusModule { }
