import {JobError} from "../../../../shared/job-error.model";

export class LoaderJob {
  id: string;
  testPackageName: string;
  createdAt: Date;
  errors: JobError[] = new Array<JobError>();
  type: string; // load or delete
  // Initialize the steps as "in progress"
  tdsStepStatus: StepStatus = StepStatus.NotApplicable;
  artStepStatus: StepStatus = StepStatus.NotApplicable;
  tisStepStatus: StepStatus = StepStatus.NotApplicable;
  thssStepStatus: StepStatus = StepStatus.NotApplicable;

  isSuccessful(): boolean {
    return this.tdsStepStatus === StepStatus.Success
      && this.artStepStatus === StepStatus.Success
      && this.tisStepStatus === StepStatus.Success
      && this.thssStepStatus === StepStatus.Success
  }
}

export enum StepStatus {
  // The default starting status - this is before the the job is placed "in progresss"
  NotApplicable = "NOT_APPLICABLE",
  NotStarted = "NOT_STARTED",
  InProgress = "IN_PROGRESS",
  Success = "SUCCESS",
  Failed = "FAILED"
}
