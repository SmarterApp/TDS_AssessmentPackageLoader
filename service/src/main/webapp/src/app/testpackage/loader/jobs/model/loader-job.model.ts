import {JobError} from "../../../../shared/job-error.model";

export class LoaderJob {
  id: number;
  testPackageName: string;
  dateCreated: string; //TODO: Date datatype?
  errors: JobError[] = new Array<JobError>();
  type: string; // create or delete
  // Initialize the steps as "in progress"
  tdsStepStatus: StepStatus = StepStatus.NotApplicable;
  artStepStatus: StepStatus = StepStatus.NotApplicable;
  tisStepStatus: StepStatus = StepStatus.NotApplicable;
  thssStepStatus: StepStatus = StepStatus.NotApplicable;

  get tdsErrors(): JobError[] {
    return this.errors.filter(err => err.system === 'TDS')
  }

  get artErrors(): JobError[] {
    return this.errors.filter(err => err.system === 'ART')
  }

  get tisErrors(): JobError[] {
    return this.errors.filter(err => err.system === 'TIS')
  }

  get thssErrors(): JobError[] {
    return this.errors.filter(err => err.system === 'THSS')
  }
}

export enum StepStatus {
  // The default starting status - this is before the the job is placed "in progresss"
  NotApplicable = "NOT_APPLICABLE",
  InProgress = "IN_PROGRESS",
  Success = "SUCCESS",
  Failed = "FAILED"
}
