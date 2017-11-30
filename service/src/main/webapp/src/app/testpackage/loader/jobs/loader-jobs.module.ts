import {NgModule} from "@angular/core";
import {DataTableModule} from "primeng/primeng";
import {LoaderJobService} from "./loader-jobs.service";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    BrowserModule,
    DataTableModule,
    FormsModule
  ],
  providers: [
    LoaderJobService
  ]
})
export class LoaderJobsModule {
}
