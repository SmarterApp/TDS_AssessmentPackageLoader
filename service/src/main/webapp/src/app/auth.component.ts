import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {User} from "./user/user";
import {UserService} from "./user/user.service";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Subscription} from "rxjs/Subscription";

@Injectable()
export class AuthGuard implements CanActivate {
  user: User = null;
  isAuthorized: boolean = false;

  private userSub: Subscription;
  private static readonly USER_REFRESH_SEC = 300;

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

  startUserTimer() {
    this.userSub = TimerObservable.create(1000 * AuthGuard.USER_REFRESH_SEC, 1000 * AuthGuard.USER_REFRESH_SEC)
      .subscribe(() => {
        this.updateUser();
      });
  }

  updateUser() {
    this.userService.getUser()
      .subscribe(user => {
        let authed = (user.permissions || []).length > 0;
        if(!authed) {
          // if not authed, stop polling until next canActivate is called
          this.userSub.unsubscribe();
          // Redirect to error page if we were logged in, but are now no longer.
          if(this.isAuthorized) {
            this.doErrorRedirect();
          }
        }
        this.user = user;
        this.isAuthorized = authed;
      });
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (!this.isAuthorized) {
      this.startUserTimer();
      return this.userService.getUser()
        .map(user => {
            this.user = user;
            this.isAuthorized = (user.permissions || []).length > 0;
            return this.redirectIfUnauthorized();
          },
          error => {
            this.user = null;
            this.isAuthorized = false;
            console.error("error fetching user info");
            this.doErrorRedirect();
            return false;
          });
    } else {
      return this.redirectIfUnauthorized();
    }
  }
}
