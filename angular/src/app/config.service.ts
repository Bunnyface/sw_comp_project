import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { Release } from './release';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  // url for mock-server
  private releasesUrl = 'api/releases';

  getReleases(): Observable<Release[]> {
    return this.http.get<Release[]>(this.releasesUrl)
      .pipe(catchError(this.handleError<Release[]>('getReleases', [])));
  }

  // Error handling
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.log(error); // log to console
      console.log(`${operation} failed: ${error.message}`); // user-friendly message
      return of(result as T); // return a dafe value to keep app running
    }
  }

  constructor(private http: HttpClient) { }
}
