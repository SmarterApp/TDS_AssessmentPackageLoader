import {Component} from "@angular/core";

@Component({
  selector: 'error',
  template: '<p class="blue msg">You have been logged out due to inactivity or are not authorized to view this page.</p><p class="blue msg">Please <a href="javascript:void(0)" onclick="window.document.logoutForm.submit()">log in again</a> or contact an administrator if this problem persists.</p><form name="logoutForm" method="post" action="saml/logout" class="hidden"></form>',
  styles: ['p.msg { padding: 0 20px}']
})
export class ErrorComponent {
}
