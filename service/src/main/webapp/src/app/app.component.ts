import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {User} from "./user/user";
import {UserService} from "./user/user.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title = 'TDS Support Tool';
  footer = '© The Regents of the University of California – Smarter Balanced Assessment Consortium';
  user: User;
  errorOccurred = false;

  constructor(private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.userService.getUser()
      .subscribe(user => {
        const hasPermissions = (user.permissions || []).length > 0;
        if (!hasPermissions) {
          this.errorOccurred = true;
          // Reroute to error page if user has no read/write permissions
          this.router.navigateByUrl('/error')
        }
        return this.user = user;
      });

  }
}
