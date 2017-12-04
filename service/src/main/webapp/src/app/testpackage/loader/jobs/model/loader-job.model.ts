import {JobError} from "../../../../shared/job-error.model";

export class LoaderJob {
  id: number;
  testPackageName: string;
  dateCreated: string; //TODO: Date datatype?
  errors: JobError[] = new Array<JobError>();
  state: string = 'Pending'; // The first state of a job is "pending"
  type: string;

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
