/**
 * Responsible for controlling the behavior of the test package loader page
 */
import {Component} from "@angular/core";
import {ScoringJob} from "./jobs/model/scoring-job.model";

@Component({
  selector: 'loader',
  templateUrl: './scoring.component.html',
  styleUrls: ['./scoring.component.scss']
})
export class ScoringComponent {
  selectedJob: ScoringJob;

  selectedScoringJobChange($event) {
    this.selectedJob = $event;
  }
}
