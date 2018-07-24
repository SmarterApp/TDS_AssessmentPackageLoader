import {ScoringJobSteps} from "./scoring-job-steps";

export class ScoringJob {
  id: string;
  shortId: string;
  examId: string;
  assessmentId: string;
  studentName: string;
  createdAt: Date;
  steps: ScoringJobSteps[];
  status: string;
  complete = false;
  originalTrtSaved = false;
}
