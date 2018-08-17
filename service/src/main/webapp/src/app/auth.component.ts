import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {User} from "./user/user";
import {UserService} from "./user/user.service";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Subscription} from "rxjs/Subscription";
import {Observable} from "rxjs/Observable";



@Injectable()
export class AuthGuard implements CanActivate {
  // These public properties are accessible from html.
  public user: User = null;
  public isAdminAuthorized: boolean = false;
  public isLoaderAuthorized: boolean = false;
  public isValidatorAuthorized: boolean = false;

  private userSub: Subscription;
  private static readonly USER_REFRESH_SEC = 300;

  constructor(private router: Router,
              private userService: UserService) {
    this.updateUser();
  }

  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.doCanActivate(route, state, "ADMIN");
  }

  // Can be called from other modules to refresh the user and 'do the right thing'.
  public updateUser() {
    this.userService.getUser()
      .subscribe(user => {
        let authed = (user.permissions || []).length > 0;
        if(!authed) {
          // if not authed, stop polling until next canActivate is called
          this.userSub.unsubscribe();
          // Redirect to error page if we were logged in, but are now no longer.
          if(this.isAdminAuthorized || this.isLoaderAuthorized || this.isValidatorAuthorized) {
            this.doErrorRedirect();
          }
        }
        this.user = user;
        this.setPermissions(user);
      });
  }

  private doErrorRedirect() {
    this.router.navigateByUrl('/loader/error');
  }

  private redirectHomeIfAuthorized(): boolean {
    if (this.isLoaderAuthorized || this.isAdminAuthorized) {
      this.router.navigateByUrl('/loader');
      return true;
    }
    if (this.isValidatorAuthorized) {
      this.router.navigateByUrl('/scoring');
      return true;
    }
    console.log("No roles authorized.");
    this.doErrorRedirect();
    return false;
  }

  private redirectIfAdminUnauthorized(): boolean {
    if (!this.isAdminAuthorized) {
      console.log("not authorized for admin, redirecting");
      this.doErrorRedirect();
    }
    return this.isAdminAuthorized;
  }

  private redirectIfLoaderUnauthorized(): boolean {
    if (!this.isLoaderAuthorized) {
      console.log("not authorized for loader, redirecting");
      this.doErrorRedirect();
    }
    return this.isLoaderAuthorized;
  }

  private redirectIfValidatorUnauthorized(): boolean {
    if (!this.isValidatorAuthorized) {
      console.log("not authorized for validator, redirecting");
      this.doErrorRedirect();
    }
    return this.isValidatorAuthorized;
  }

  private startUserTimer() {
    this.userSub = TimerObservable.create(1000 * AuthGuard.USER_REFRESH_SEC, 1000 * AuthGuard.USER_REFRESH_SEC)
      .subscribe(() => {
        this.updateUser();
      });
  }

  private setPermissions(user: User) {
    this.isAdminAuthorized = (user.permissions || []).length > 0 &&
      user.permissions.includes("PERM_SUPPORT_TOOL_ADMINISTRATION");
    this.isLoaderAuthorized = (user.permissions || []).length > 0 &&
      (user.permissions.includes("PERM_TEST_PACKAGE_LOADER") ||
        user.permissions.includes("PERM_SUPPORT_TOOL_ADMINISTRATION"));
    this.isValidatorAuthorized = (user.permissions || []).length > 0 &&
      (user.permissions.includes("PERM_SCORING_VALIDATOR") ||
        user.permissions.includes("PERM_SUPPORT_TOOL_ADMINISTRATION"));
  }

  private redirectForRealmIfUnauthorized(realm: String) {
    switch(realm) {
      case "HOME":
        return this.redirectHomeIfAuthorized();
      case "LOADER":
        return this.redirectIfLoaderUnauthorized();
      case "VALIDATOR":
        return this.redirectIfValidatorUnauthorized();
      default:
        return this.redirectIfAdminUnauthorized();
    }
  }

  protected doCanActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot, realm: String): Observable<boolean> {
    if (!this.isAdminAuthorized && !this.isLoaderAuthorized && !this.isValidatorAuthorized) {
      this.startUserTimer();
      return this.userService.getUser()
        .map(user => {
            this.user = user;
            this.setPermissions(user);
            return this.redirectForRealmIfUnauthorized(realm);
          },
          error => {
            this.user = null;
            this.isAdminAuthorized = false;
            this.isLoaderAuthorized = false;
            this.isValidatorAuthorized = false;
            console.error("error fetching user info");
            this.doErrorRedirect();
            return false;
          });
    } else {
      return Observable.of(this.redirectForRealmIfUnauthorized(realm));
    }
  }
}

@Injectable()
export class HomeAuthGuard extends AuthGuard {
  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.doCanActivate(route, state, "HOME");
  }
}

@Injectable()
export class LoaderAuthGuard extends AuthGuard {
  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.doCanActivate(route, state, "LOADER");
  }
}

@Injectable()
export class ValidatorAuthGuard extends AuthGuard {
  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.doCanActivate(route, state, "VALIDATOR");
  }
}
