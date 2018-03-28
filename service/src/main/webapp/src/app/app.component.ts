import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {User} from "./user/user";
import {UserService} from "./user/user.service";
import {_throw} from "rxjs/observable/throw";
import {catchError} from "rxjs/operators";
import {forkJoin} from "rxjs/observable/forkJoin";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title = 'TDS Support Tool';
  footer = '© The Regents of the University of California – Smarter Balanced Assessment Consortium';
  user: User;

  constructor(private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.userService.getUser()
      .subscribe(user => this.user = user);

  }
}
