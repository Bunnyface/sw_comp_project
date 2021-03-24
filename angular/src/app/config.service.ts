import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { Release } from './release';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private releasesUrl = 'http://localhost:8081/releases';
  private comparisonUrl = 'http://localhost:8081/comparison'

  // Get all releases
  getReleases(): Observable<any> {
    return this.http.get<any>(this.releasesUrl)
      .pipe(catchError(this.handleError<String[]>('getReleases', [])));
  }

  // Get details of selected release
  getRelease(name: string): Observable<any> {
    const url = `${this.releasesUrl}/${name}`;
    return this.http.get<any>(url)
      .pipe(catchError(this.handleError<any>(`getRelease id=${name}`)));
  }

  // Get comparison of two selected releases
  getComparison(first: string, second: string): Observable<any> {
    const url = `${this.comparisonUrl}/${first}:${second}`
    return this.http.get<any>(url)
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
