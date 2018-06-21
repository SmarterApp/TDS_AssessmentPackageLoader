import {Component, Input} from "@angular/core";
import {ScoringJob} from "./model/scoring-job.model";

@Component({
  selector: 'scoring-job-details',
  templateUrl: './scoring-job-details.component.html'
})
export class ScoringJobDetailsComponent {
  @Input()
  selectedJob: ScoringJob;
}
