
import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import {RouterModule} from "@angular/router";
import {DataService} from "./data/data.service";

@NgModule({
  declarations: [
  ],
  imports: [
    HttpClientModule,
    FormsModule,
    BrowserModule
  ],
  exports: [
    RouterModule
  ],
  providers: [
    DataService,
    HttpClientModule
  ]
})
export class CommonModule {
}
