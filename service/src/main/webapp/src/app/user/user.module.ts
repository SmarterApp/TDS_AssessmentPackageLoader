import { NgModule } from "@angular/core";
import { CommonModule } from "../shared/common.module";
import { UserService } from "./user.service";
import { BrowserModule } from "@angular/platform-browser";

@NgModule({
  imports: [
    BrowserModule,
    CommonModule
  ],
  providers: [
    UserService
  ]
})
export class UserModule {
}
