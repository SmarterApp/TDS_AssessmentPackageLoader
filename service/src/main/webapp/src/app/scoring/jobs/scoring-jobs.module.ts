import {NgModule} from "@angular/core";
import {ScoringJobService} from "./scoring-jobs.service";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [
    ScoringJobService
  ]
})
export class ScoringJobsModule {
}
