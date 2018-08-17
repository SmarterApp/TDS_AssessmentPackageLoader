import {NgModule} from "@angular/core";
import {ScoringJobService} from "./scoring-jobs.service";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ModalModule} from "ngx-bootstrap";

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    ModalModule.forRoot()
  ],
  providers: [
    ScoringJobService
  ]
})
export class ScoringJobsModule {
}
