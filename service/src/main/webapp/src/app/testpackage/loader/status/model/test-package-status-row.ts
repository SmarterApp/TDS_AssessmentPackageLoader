import { StepStatus } from "../../jobs/model/test-package-job.model";

/**
 * The UI representation of a {TestPackageStatus} record from the server.  This "flattens out" the {TestPackageStatus}
 * so it can be displayed as a single row in a table of test package statuses.
 */
export class TestPackageStatusRow {

  private _name: string;
  private _uploadedAt: Date;
  private _tdsStatus: StepStatus;
  private _artStatus: StepStatus;
  private _tisStatus: StepStatus;
  private _thssStatus: StepStatus;

  constructor(builder: TestPackageStatusRowBuilder) {
    this._name = builder.name;
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

  /**
   * Determine which ic on should be displayed based on the {StepStatus} value
   *
   * @param {StepStatus} status the {StepStatus} to evaluate
   * @return {string} The CSS class to display the appropriate icon
   */
  static getStatusIconClass(status: StepStatus): string {
    let cssClass = "fa fa-minus";

    switch (status) {
      case StepStatus.Success:
        cssClass = "fa fa-check-circle";
        break;
      case StepStatus.Fail:
        cssClass = "fa fa-exclamation-circle";
        break;
      default:
        break;
    }

    return cssClass;
  }
}

/**
 * A builder for constructing a new {TestPackageStatusRow}.
 */
export class TestPackageStatusRowBuilder {
  private _name: string;
  private _uploadedAt: Date;
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
