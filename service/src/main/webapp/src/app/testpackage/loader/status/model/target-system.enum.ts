/**
 * Describes all of the available "downstream" systems that the Support Tool loads test packages into.
 */
export enum TargetSystem {
  /**
   * Administration and Registration Tools
   */
  ART = "ART",

  /**
   * The Test Delivery System
   */
  TDS = "TDS",

  /**
   * The Test Integration System
   */
  TIS = "TIS",

  /**
   * The Teacher Hand-Scoring System
   */
  THSS = "THSS"
}
