import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { Release } from './release';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private releasesUrl = 'http://172.18.0.2:8081/releases';
  private comparisonUrl = 'http://scala/comparison'

  getReleases(): Observable<any> {
    return this.http.get<any>(this.releasesUrl)
      .pipe(catchError(this.handleError<String[]>('getReleases', [])));
  }

  getComparison(): Observable<any[]> {
    return this.http.get<any[]>(this.comparisonUrl)
    .pipe(catchError(this.handleError<any>('getComparison', [])));
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
