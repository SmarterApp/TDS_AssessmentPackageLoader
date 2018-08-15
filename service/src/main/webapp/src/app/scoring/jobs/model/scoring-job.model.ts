import {ScoringJobSteps} from "./scoring-job-steps";
import {StepStatus} from "../../../testpackage/loader/jobs/model/test-package-job.model";

export class ScoringJob {
  id: string;
  shortId: string;
  examId: string;
  assessmentId: string;
  studentName: string;
  createdAt: Date;
  steps: ScoringJobSteps[];
  complete = false;
  originalTrtSaved = false;

  private _status: string;

  get status(): string {
    if (this._status === 'IN_PROGRESS' && this.isExpired()) {
      return "FAIL - EXPIRED";
    }

    return this._status;
  }

  set status(newStatus: string) {
    this._status = newStatus;
  }

  isExpired(): boolean {
    // Check if 5 minutes have elapsed
    return this.createdAt.getTime() < new Date().getTime() - (1000 * 60 * 5);
  }
}
