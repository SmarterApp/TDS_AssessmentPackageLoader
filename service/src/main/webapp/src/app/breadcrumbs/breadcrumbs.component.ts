import {Component, OnInit} from "@angular/core";
import { Title } from "@angular/platform-browser";
import { Router, ActivatedRoute, NavigationEnd, PRIMARY_OUTLET, UrlSegment } from "@angular/router";
import "rxjs/add/operator/filter";

@Component({
  selector: 'breadcrumbs',
  templateUrl: './breadcrumbs.component.html'
})
export class BreadcrumbsComponent implements OnInit{
  breadcrumbs : Array<any> = [];

  constructor(private router: Router, private activatedRoute : ActivatedRoute, private title: Title) {
  }

  ngOnInit(): void {
    this.router.events.filter(event => event instanceof NavigationEnd).subscribe(() => {
      let root: ActivatedRoute = this.activatedRoute.root;
      let translatedBreadCrumbs = "";
      this.breadcrumbs = this.getBreadcrumbs(root);
      for (let i = this.breadcrumbs.length - 1; i >= 0; i--) {
        translatedBreadCrumbs = translatedBreadCrumbs.concat(this.breadcrumbs[i].label, " < ");
      }
      this.title.setTitle(translatedBreadCrumbs + "Smarter Balanced | Support Tools");
    });
  }

  private getBreadcrumbs(route: ActivatedRoute, commands: any[]=[], breadcrumbs: any[] = []): any[] {
    let BreadcrumbsKeyword = "breadcrumb";

    let children: ActivatedRoute[] = route.children;
    if (children.length === 0 ) {
      return breadcrumbs;
    }

    for (let child of children) {
      if (child.outlet != PRIMARY_OUTLET ) {
        continue; // skip
      }

      if (!child.snapshot.data.hasOwnProperty(BreadcrumbsKeyword)) {
        return this.getBreadcrumbs(child, commands, breadcrumbs);
      }

      // Parse the route commands for this route
      let crumbData = child.snapshot.data[BreadcrumbsKeyword];
      let route = child.snapshot;
      let urlSegments: UrlSegment[] = route.url;
      urlSegments.forEach(segment => {
        commands.push(segment.path);
        commands.push(segment.parameters);
      });
      // let routeCommands = _.clone(commands);

      let breadcrumb: any = {
        label: crumbData.label,
        commands: commands
      };

      // // let existing = breadcrumbs.find(x => _.isEqual(x.commands, breadcrumb.commands));
      // //
      // // if(existing) {
      // //   existing.label = breadcrumb.label;
      // // }
      // else {
        breadcrumbs.push(breadcrumb);
      // }

      //recursive
      return this.getBreadcrumbs(child, commands, breadcrumbs);
    }
  }
}
