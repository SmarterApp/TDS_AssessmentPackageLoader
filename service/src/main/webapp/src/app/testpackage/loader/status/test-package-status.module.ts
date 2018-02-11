import { NgModule } from '@angular/core';
import { ConfirmationService, ConfirmDialogModule, DataTableModule } from "primeng/primeng";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule } from "@angular/forms";
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
    FormsModule
  ],
  providers: [
    TestPackageStatusService,
    ConfirmationService
  ]
})
export class TestPackageStatusModule {
}
