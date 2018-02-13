import { NgModule } from '@angular/core';
import { ConfirmationService, ConfirmDialogModule, DataTableModule, PaginatorModule } from "primeng/primeng";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { TestPackageStatusService } from "./service/test-package-status.service";
import { TestPackageStatusComponent } from "./test-package-status.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    TestPackageStatusComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    DataTableModule,
    ConfirmDialogModule,
    PaginatorModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    TestPackageStatusService,
    ConfirmationService
  ]
})
export class TestPackageStatusModule {
}
