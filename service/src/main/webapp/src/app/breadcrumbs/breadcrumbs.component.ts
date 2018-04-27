import { Component, OnInit } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { ActivatedRoute, NavigationEnd, PRIMARY_OUTLET, Router } from "@angular/router";
import "rxjs/add/operator/filter";
import * as _ from "lodash";

export const BreadCrumbsRouteDataKey = 'breadcrumb';
export const BreadCrumbsTitleDelimiter = ' < ';

/**
 * Interface for defining breadcrumbs in the route data param.
 *
 * Example usages:
 * data: { breadcrumb: { label: 'My View' }
 */
export interface BreadcrumbOptions {
  label: string;
}

/**
 * Represents a breadcrumb in the navigation path
 */
export interface Breadcrumb {

  /**
   * Display name of the path segment
   */
  text: string;

  /**
   * routeLink directive parameters
   * This is an array of the following format: ['/path', param, '/path', param]
   *
   * @see @angular/router/RouterLink
   */
  routerLinkParameters: any[];
}

@Component({
  selector: 'sb-breadcrumbs',
  template: `
    <div [hidden]="breadcrumbs.length == 0">
      <div class="container">
        <ul class="breadcrumb">
          <li>
            <a routerLink="/">
              <i class="fa fa-home"></i> <span class="sr-only">{{ 'common-ngx.breadcrumbs.home-sr' }}</span>
            </a>
          </li>
          <li *ngFor="let crumb of breadcrumbs; let last = last;" [ngClass]="{'active': last }">
            <a *ngIf="!last" [routerLink]="crumb.routerLinkParameters">{{ crumb.text }}</a>
            <span *ngIf="last" [routerLink]="crumb.routerLinkParameters">{{ crumb.text }}</span>
          </li>
        </ul>
      </div>
    </div>
  `
})
export class BreadcrumbsComponent implements OnInit {

  private _breadcrumbs: Breadcrumb[] = [];

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private title: Title) {
  }

  ngOnInit(): void {
    this.router.events
      .filter(event => event instanceof NavigationEnd)
      .subscribe(() => {
        this.breadcrumbs = this.createBreadcrumbs(this.activatedRoute.root);
      });
  }

  get breadcrumbs(): Breadcrumb[] {
    return this._breadcrumbs;
  }

  set breadcrumbs(values: Breadcrumb[]) {
    if (this._breadcrumbs !== values) {
      this._breadcrumbs = values;
      this.title.setTitle(this.createTitle(values));
    }
  }

  private createBreadcrumbs(route: ActivatedRoute, routerLinkParameters: any[] = [], breadcrumbs: Breadcrumb[] = []): Breadcrumb[] {

    const children: ActivatedRoute[] = route.children;
    if (children.length === 0) {
      return breadcrumbs;
    }

    for (let child of children) {
      if (child.outlet != PRIMARY_OUTLET) {
        continue;
      }

      const route = child.snapshot;

      if (!route.data.hasOwnProperty(BreadCrumbsRouteDataKey)) {
        return this.createBreadcrumbs(child, routerLinkParameters, breadcrumbs);
      }

      const breadcrumbOptions: BreadcrumbOptions = route.data[ BreadCrumbsRouteDataKey ];

      route.url.forEach(segment => {
        routerLinkParameters.push(segment.path);
        routerLinkParameters.push(segment.parameters);
      });

      const breadcrumb = this.createBreadcrumb(breadcrumbOptions, route.data, routerLinkParameters.concat());
      const existing = breadcrumbs.find(existing => _.isEqual(existing.routerLinkParameters, breadcrumb.routerLinkParameters));

      if (existing) {
        existing.text = breadcrumb.text;
      } else {
        breadcrumbs.push(breadcrumb);
      }

      return this.createBreadcrumbs(child, routerLinkParameters, breadcrumbs);
    }
  }

  private createBreadcrumb(options: BreadcrumbOptions, routeData: any, routerLinkParameters: any[]): Breadcrumb {
      return {
        text: options.label,
        routerLinkParameters: routerLinkParameters
      }
  }

  private createTitle(breadcrumbs: any[]): string {
    return breadcrumbs
      .concat()
      .reverse()
      .map(breadcrumb => breadcrumb.text)
      .join(BreadCrumbsTitleDelimiter);
  }

  private getPropertyValue(propertyPath: string, object: any): any {
    let parts = propertyPath.split('.'),
      property = object || this;

    for (let i = 0; i < parts.length; i++) {
      property = property[ parts[ i ] ];
    }
    return property;
  }

}
