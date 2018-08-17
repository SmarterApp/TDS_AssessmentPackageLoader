import { StepStatus } from "../../jobs/model/test-package-job.model";

/**
 * The UI representation of a {TestPackageStatus} record from the server.  This "flattens out" the {TestPackageStatus}
 * so it can be displayed as a single row in a table of test package statuses.
 */
export class TestPackageStatusRow {

  private _name: string;
  private _uploadedAt: Date;
  private _jobId: string;
  private _jobType: string;
  private _tdsStatus: StepStatus;
  private _artStatus: StepStatus;
  private _tisStatus: StepStatus;
  private _thssStatus: StepStatus;

  constructor(builder: TestPackageStatusRowBuilder) {
    this._name = builder.name;
    this._jobId = builder.jobId;
    this._jobType = builder.jobType;
    this._uploadedAt = builder.uploadedAt;
    this._tdsStatus = builder.tdsStatus;
    this._artStatus = builder.artStatus;
    this._tisStatus = builder.tisStatus;
    this._thssStatus = builder.thssStatus;
  }

  /**
   * @return {string} The name of the {TestPackage}.  Typically this value will be the name of the test package XML file
   * that was uploaded.
   */
  get name(): string {
    return this._name;
  }

  /**
   * @return {Date} The most recent date/time when the test package file was uploaded.
   */
  get uploadedAt(): Date {
    return this._uploadedAt;
  }

  /**
   * @return {string} The unique identifier of the job that most recently operated on the {TestPackage} this status
   * record represents.
   */
  get jobId(): string {
    return this._jobId;
  }

  /**
   * @return {string} The type of job that most recently operated on the {TestPackage} this status record represents.
   */
  get jobType(): string {
    return this._jobType;
  }

  /**
   * @return {StepStatus} The {StepStatus} describing the state pf this test package in TDS (i.e. the itembank and
   * configs databases}.
   */
  get tdsStatus(): StepStatus {
    return this._tdsStatus;
  }

  /**
   * @return {StepStatus} The {StepStatus} describing the state pf this test package in ART
   */
  get artStatus(): StepStatus {
    return this._artStatus;
  }

  /**
   * @return {StepStatus} The {StepStatus} describing the state pf this test package in TIS
   */
  get tisStatus(): StepStatus {
    return this._tisStatus;
  }

  /**
   * @return {StepStatus} The {StepStatus} describing the state pf this test package in THSS
   */
  get thssStatus(): StepStatus {
    return this._thssStatus;
  }
}

/**
 * A builder for constructing a new {TestPackageStatusRow}.
 */
export class TestPackageStatusRowBuilder {
  private _name: string;
  private _uploadedAt: Date;
  private _jobId: string;
  private _jobType: string;
  private _tdsStatus: StepStatus;
  private _artStatus: StepStatus;
  private _tisStatus: StepStatus;
  private _thssStatus: StepStatus;

  constructor(name: string) {
    this._name = name;
  }

  get name(): string {
    return this._name;
  }

  get uploadedAt(): Date {
    return this._uploadedAt;
  }

  withUploadedAt(uploadedAt: Date) {
    this._uploadedAt = uploadedAt;
    return this;
  }

  get jobId(): string {
    return this._jobId;
  }

  withJobId(jobId: string) {
    this._jobId = jobId;
    return this;
  }

  get jobType(): string {
    return this._jobType;
  }

  withJobType(jobType: string) {
    this._jobType = jobType;
    return this;
  }

  get tdsStatus(): StepStatus {
    return this._tdsStatus;
  }

  withTdsStatus(tdsStatus: StepStatus) {
    this._tdsStatus = tdsStatus;
    return this;
  }

  get artStatus(): StepStatus {
    return this._artStatus;
  }

  withArtStatus(artStatus: StepStatus) {
    this._artStatus = artStatus;
    return this;
  }

  get tisStatus(): StepStatus {
    return this._tisStatus;
  }

  withTisStatus(tisStatus: StepStatus) {
    this._tisStatus = tisStatus;
    return this;
  }

  get thssStatus(): StepStatus {
    return this._thssStatus;
  }

  withThssStatus(thssStatus: StepStatus) {
    this._thssStatus = thssStatus;
    return this;
  }

  build(): TestPackageStatusRow {
    return new TestPackageStatusRow(this);
  }
}
