import {Routes} from "@angular/router";
import {TestPackageComponent} from "./testpackage/loader/test-package.component";
import {UploadLoaderJobComponent} from "./testpackage/loader/jobs/upload/upload-loader-job.component";
import {TestPackageStatusComponent} from "./testpackage/loader/status/test-package-status.component";
import {ErrorComponent} from "./error.component";
import {HomeAuthGuard, LoaderAuthGuard, ValidatorAuthGuard} from "./auth.component";
import {ScoringComponent} from "./scoring/scoring.component";
import {UploadScoringJobComponent} from "./scoring/jobs/upload/upload-scoring-job.component";
import {AppComponent} from "./app.component";

export const routes: Routes = [
  {
    path: 'home',
    canActivate: [HomeAuthGuard],
    component: AppComponent,
    pathMatch: 'full'
  },
  {
    path: '',
    canActivate: [HomeAuthGuard],
    component: AppComponent,
    pathMatch: 'full',
  },
  {
    path: 'loader',
    pathMatch: 'prefix',
    data: {
      breadcrumb: {
        label: "Test Package Jobs"
      }
    },
    children: [
      {
        path: '',
        pathMatch: 'prefix',
        canActivate: [LoaderAuthGuard],
        component: TestPackageComponent
      },
      {
        path: 'error',
        pathMatch: 'prefix',
        component: ErrorComponent
      },
      {
        path: 'upload',
        pathMatch: 'prefix',
        data: {
          breadcrumb: {
            label: "Test Package Upload"
          }
        },
        children: [
          {
            path: '',
            pathMatch: 'prefix',
            canActivate: [LoaderAuthGuard],
            component: UploadLoaderJobComponent
          }
        ]
      }, {
        path: 'status',
        pathMatch: 'prefix',
        canActivate: [LoaderAuthGuard],
        component: TestPackageStatusComponent,
        data: {
          breadcrumb: {
            label: "Loaded Test Packages"
          }
        }
      }
    ]
  },
  {
    path: 'scoring',
    pathMatch: 'prefix',
    data: {
      breadcrumb: {
        label: "Scoring Validation Jobs"
      }
    },
    children: [
      {
        path: '',
        pathMatch: 'prefix',
        canActivate: [ValidatorAuthGuard],
        component: ScoringComponent
      },
      {
        path: 'error',
        pathMatch: 'prefix',
        component: ErrorComponent
      },
      {
        path: 'upload',
        pathMatch: 'prefix',
        data: {
          breadcrumb: {
            label: "Test Results Upload"
          }
        },
        children: [
          {
            path: '',
            pathMatch: 'prefix',
            canActivate: [ValidatorAuthGuard],
            component: UploadScoringJobComponent
          }
        ]
      }
    ]
  }
];
