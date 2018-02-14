import {Observable} from "rxjs/Observable";
// import {Http, RequestOptionsArgs} from "@angular/http"; //TODO: Deprecated in Angular 4+, switch to angular/common/http
import {Injectable} from "@angular/core";
import "rxjs/add/operator/map";
import { HttpParams, HttpClient } from '@angular/common/http';
import { RequestOptionsArgs } from "@angular/http";
import { RequestOptions } from "http";

@Injectable()
export class DataService {

  constructor(private http: HttpClient)  {
  }

  get(url, options?: any): Observable<any> {
    return this.http
      .get(`/api${url}`, options);
  }

  delete(url, options?: any): Observable<any> {
    return this.http
      .delete(`/api${url}`, options);
  }
}
