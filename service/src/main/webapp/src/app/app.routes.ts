import {Routes} from "@angular/router";
import {LoaderComponent} from "./testpackage/loader/loader.component";
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
            component: LoaderComponent
          }, {
            path: 'upload',
            pathMatch: 'prefix',
            component: UploadLoaderJobComponent,
            data: {
              breadcrumb: {
                label: "Create Test Package Loader Jobs"
              }
            }
          }
        ]
      }
    ]
  }
];
