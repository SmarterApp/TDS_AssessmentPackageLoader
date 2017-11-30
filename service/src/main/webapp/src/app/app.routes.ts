import { Routes } from "@angular/router";
import {LoaderComponent} from "./testpackage/loader/loader.component";

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
        component: LoaderComponent,
        data: {
          breadcrumb: {
            label: "Load Test Package Jobs"
          }
        }
      }
    ]
  }
];
