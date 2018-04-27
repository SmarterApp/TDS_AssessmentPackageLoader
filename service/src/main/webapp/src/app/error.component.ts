import {Component} from "@angular/core";

@Component({
  selector: 'error',
  template: '<h3 class="error">You are not authorized to view the TDS Support Tool application. Please contact an administrator</h3>',
  styles: ['h3.error { padding: 20px; color: #CC0000; }']
})
export class ErrorComponent {
}
