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
  isAuthorized: boolean;
  errorOccurred = false;

  constructor(private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.userService.getUser()
      .subscribe(user => {
        this.isAuthorized = (user.permissions || []).length > 0;
        this.errorOccurred = !this.isAuthorized;
        return this.user = user;
      });

  }
}
