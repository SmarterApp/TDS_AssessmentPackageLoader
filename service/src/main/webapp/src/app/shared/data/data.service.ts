import { Observable } from "rxjs/Observable";
import { Injectable } from "@angular/core";
import "rxjs/add/operator/map";
import { HttpClient } from '@angular/common/http';

@Injectable()
export class DataService {

  constructor(private http: HttpClient) {
  }

  get(url, options?: any): Observable<any> {
    return this.http
      .get(`api${url}`, options);
  }

  delete(url, options?: any): Observable<any> {
    return this.http
      .delete(`api${url}`, options);
  }
}
