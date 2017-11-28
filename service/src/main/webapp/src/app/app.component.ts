import {Component} from "@angular/core";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'TDS Support Tool';
  footer = '© The Regents of the University of California – Smarter Balanced Assessment Consortium';

  constructor(private router: Router) {
  }
}
