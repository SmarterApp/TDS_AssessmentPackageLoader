import {ScoringJobSteps} from "./scoring-job-steps";
import {StepStatus} from "../../../testpackage/loader/jobs/model/test-package-job.model";

export class ScoringJob {
  id: string;
  shortId: string;
  createdAt: Date;
  steps: ScoringJobSteps[];
  complete = false;
  originalTrtSaved = false;

  private _status: string;
  private _examId: string;
  private _assessmentId: string;
  private _studentName: string;

  get examId(): string {
    if (this._examId) {
      return this._examId;
    }

    return "----"
  }

  set examId(newExamId: string) {
    this._examId = newExamId;
  }

  get assessmentId(): string {
    if (this._assessmentId) {
      return this._assessmentId;
    }

    return "----"
  }

  set assessmentId(newAssessmentId: string) {
    this._assessmentId = newAssessmentId;
  }

  get studentName(): string {
    if (this._studentName) {
      return this._studentName;
    }

    return "----"
  }

  set studentName(newStudentName: string) {
    this._studentName = newStudentName;
  }
  
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
