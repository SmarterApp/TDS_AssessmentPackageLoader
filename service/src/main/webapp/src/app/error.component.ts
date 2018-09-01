import {Component} from "@angular/core";

@Component({
  selector: 'error',
  template: '<h3 class="blue">You have been logged out due to inactivity, did not successfully log in,<br>or are not authorized to view this page.</h3><p class="msg">Please <a href="javascript:void(0)" onclick="window.document.logoutForm.submit()">log in again</a> or contact an administrator if this problem persists.</p><form name="logoutForm" method="post" action="saml/logout" class="hidden"></form>',
  styles: []
})
export class ErrorComponent {
}
