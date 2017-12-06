import {Observable} from "rxjs/Observable";
// import {Http, RequestOptionsArgs} from "@angular/http"; //TODO: Deprecated in Angular 4+, switch to angular/common/http
import {Injectable} from "@angular/core";
import "rxjs/add/operator/map";
import { HttpParams, HttpClient } from '@angular/common/http';

@Injectable()
export class DataService {

  constructor(private http: HttpClient)  {
  }

  get(url, options?: HttpParams): Observable<any> {
    return this.http
      .get(`/api${url}`, options)
      .map(response => response.json());
  }

  // delete(url, options?: RequestOptionsArgs): Observable<any> {
  //   return this.http
  //     .delete(`/api${url}`, options)
  //     .map(response => response.json());
  // }
}
