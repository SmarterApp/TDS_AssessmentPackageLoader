
import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {DataService} from "./data/data.service";

@NgModule({
  declarations: [
  ],
  imports: [
    HttpModule,
    FormsModule,
    BrowserModule
  ],
  exports: [
    RouterModule
  ],
  providers: [
    DataService
  ]
})
export class CommonModule {
}
