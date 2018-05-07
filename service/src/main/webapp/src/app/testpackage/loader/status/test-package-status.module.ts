import {NgModule} from '@angular/core';
import {ConfirmationService, ConfirmDialogModule} from "primeng/primeng";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TestPackageStatusService} from "./service/test-package-status.service";
import {TestPackageStatusComponent} from "./test-package-status.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";

@NgModule({
  declarations: [
    TestPackageStatusComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ConfirmDialogModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule
  ],
  providers: [
    TestPackageStatusService,
    ConfirmationService
  ]
})
export class TestPackageStatusModule {
}
