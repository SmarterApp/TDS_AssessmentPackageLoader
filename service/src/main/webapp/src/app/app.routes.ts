import {Routes} from "@angular/router";
import {TestPackageComponent} from "./testpackage/loader/test-package.component";
import {UploadLoaderJobComponent} from "./testpackage/loader/jobs/upload/upload-loader-job.component";

export const routes: Routes = [
  {
    path: 'home',
    redirectTo: '',
    pathMatch: 'full'
  },
  {
    path: '',
    children: [
      {
        path: 'loader',
        pathMatch: 'prefix',
        data: {
          breadcrumb: {
            label: "Load Test Package Jobs"
          }
        },
        children: [
          {
            path: '',
            pathMatch: 'prefix',
            component: TestPackageComponent
          }, {
            path: 'upload',
            pathMatch: 'prefix',
            data: {
              breadcrumb: {
                label: "Create Test Package Loader Jobs"
              }
            },
            children: [
              {
                path: '',
                pathMatch: 'prefix',
                component: UploadLoaderJobComponent
              }
            ]
          }
        ]
      }
    ]
  }
];
