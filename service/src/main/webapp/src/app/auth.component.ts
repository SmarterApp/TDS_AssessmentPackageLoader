import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {User} from "./user/user";
import {UserService} from "./user/user.service";

@Injectable()
export class AuthGuard implements CanActivate {

  user: User = null;
  isAuthorized: boolean = false;

  constructor(private router: Router,
              private userService: UserService) {
  }

  doErrorRedirect() {
    console.log("not authorized, redirecting");
    this.router.navigateByUrl('/loader/error');
  }

  redirectIfUnauthorized(): boolean {
    // Reroute to error page if user has no read/write permissions
    if (!this.isAuthorized) {
      this.doErrorRedirect();
    }
    return this.isAuthorized;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (!this.user) {
      return this.userService.getUser()
        .map(user => {
            this.user = user;
            this.isAuthorized = (user.permissions || []).length > 0;
            return this.redirectIfUnauthorized();
          },
          error => {
            console.error("could not fetch user info - redirecting to error screen");
            this.doErrorRedirect();
            return false;
          });
    } else {
      return this.redirectIfUnauthorized();
    }
  }
}
