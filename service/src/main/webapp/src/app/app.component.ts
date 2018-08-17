import {Component} from "@angular/core";
import {AuthGuard} from "./auth.component";
import {User} from "./user/user";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'TDS Support Tool';
  footer = '© The Regents of the University of California – Smarter Balanced Assessment Consortium';

  constructor(private authGuard: AuthGuard) {
  }

  get adminAuthorized():boolean {
    return this.authGuard.isAdminAuthorized;
  }

  get validatorAuthorized():boolean {
    return this.authGuard.isValidatorAuthorized;
  }

  get loaderAuthorized():boolean {
    return this.authGuard.isLoaderAuthorized;
  }

  get user():User {
    return this.authGuard.user;
  }
}
