import {Component, Input, TemplateRef} from "@angular/core";
import {ScoringJob} from "./model/scoring-job.model";
import {ScoringJobService} from "./scoring-jobs.service";
import {BsModalRef, BsModalService} from "ngx-bootstrap";

@Component({
  selector: 'scoring-job-details',
  templateUrl: './scoring-job-details.component.html'
})
export class ScoringJobDetailsComponent {
  modalRef: BsModalRef;
  @Input()
  selectedJob: ScoringJob;
  scoringReport: any;

  constructor(private service: ScoringJobService,
              private modalService: BsModalService) {
  }

  downloadOriginalTrt(jobId: string) {
    this.service.getOriginalTrt(jobId)
      .subscribe(data => this.downloadFile(jobId, data, 'trt-original'),
        error => console.log("Error downloading the original trt file."),
        () => console.log('Completed original trt file download.'));
  }

  downloadRescoredTrt(jobId: string) {
    this.service.getRescoredTrt(jobId)
      .subscribe(data => this.downloadFile(jobId, data, 'trt-rescored'),
        error => console.log("Error downloading the rescored trt file."),
        () => console.log('Completed rescored trt file download.'));
  }

  downloadScoringValidationReport(jobId: string) {
    this.service.getScoringValidationReportRaw(jobId)
      .subscribe(data => this.downloadFile(jobId, data, 'scoring-report'),
        error => console.log("Error downloading the scoring validation report file."),
        () => console.log('Completed download of scoring validation report'));
  }

  downloadFile(jobId: string, data: string, fileNamePrefix: string) {
    // A bit of a hack - create an invisible anchor to execute the download
    let link = document.createElement("a");
    link.href = 'data:text/xml,' + encodeURIComponent(data);
    link.download = `${fileNamePrefix}-${jobId}.xml`;
    link.click();
  }

  openScoringValidationModal(template: TemplateRef<any>) {
    this.service.getScoringValidationReport(this.selectedJob.id)
      .subscribe(report => this.scoringReport = report);
    this.modalRef = this.modalService.show(template);
  }

  formatDate(val: string): string {
    return new Date(val).toLocaleString();
  }
}
