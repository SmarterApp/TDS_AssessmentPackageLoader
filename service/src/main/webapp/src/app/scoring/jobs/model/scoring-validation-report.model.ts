export class ScoringValidationReport {
  jobId: string;
  differenceReport: {
    test?: {
      testId: {
        from?: string,
        to?: string
      },
      name: {
        from?: string,
        to?: string
      }
    },
    examinee?: {
      key: {
        from?: number,
        to?: number
      }
    },
    opportunity?: {
      scores: [{
        status: string,
        identifier: {
          measureLabel: string,
          measureOf: string
        },
        value: {
          from?: string,
          to?: string
        },
        standardError: {
          from?: string,
          to?: string
        }
      }],
      items: [{
        status: string,
        identifier: {
          position: number,
          bankKey: number,
          key: number
        },
        score: {
          from: string,
          to: string
        },
        scoreStatus: {
          from: string,
          to: string
        }
      }]
    }
  };
}
