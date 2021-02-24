import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

import { Release } from './release';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  // url for mock-server
  private releasesUrl = 'api/releases';

  getReleases(): Observable<Release[]> {
    return this.http.get<Release[]>(this.releasesUrl)
  }

  constructor(private http: HttpClient) { }
}
