import {JobError} from "../../../../shared/job-error.model";

export class TestPackageJob {
  id: string;
  testPackageName: string;
  createdAt: Date;
  errors: JobError[] = new Array<JobError>();
  type: string; // load/delete/rollback
  parentJobId: string; // Used to identify which loader job triggered a rollback
  // Initialize the steps as "in progress"
  tdsStepStatus: StepStatus = StepStatus.NotApplicable;
  artStepStatus: StepStatus = StepStatus.NotApplicable;
  tisStepStatus: StepStatus = StepStatus.NotApplicable;
  thssStepStatus: StepStatus = StepStatus.NotApplicable;

  isSuccessful(): boolean {
    return (this.tdsStepStatus === StepStatus.Success || this.tdsStepStatus === StepStatus.NotApplicable)
      && (this.artStepStatus === StepStatus.Success || this.artStepStatus === StepStatus.NotApplicable)
      && (this.tisStepStatus === StepStatus.Success || this.tisStepStatus === StepStatus.NotApplicable)
      && (this.thssStepStatus === StepStatus.Success || this.thssStepStatus === StepStatus.NotApplicable);
  }
}

export enum StepStatus {
  // The default starting status - this is before the the job is placed "in progresss"
  NotApplicable = "NOT_APPLICABLE",
  NotStarted = "NOT_STARTED",
  InProgress = "IN_PROGRESS",
  Success = "SUCCESS",
  Fail = "FAIL"
}
