import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { Release } from './release';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private url = 'http://localhost:8081';

  // Get all releases
  getReleases(): Observable<any> {
    const url = `${this.url}/releases`;
    const body = {};
    return this.http.post(url,null)
      .pipe(catchError(this.handleError<any>('getReleases', [])));
  }

  // Get details of selected release
  getRelease(name: string): Observable<any> {
    const url = `${this.url}/releases/${name}`;
    const body = {};
    return this.http.post(url,null)
      .pipe(catchError(this.handleError<any>(`getRelease id=${name}`)));
  }

  // Get comparison of two selected releases
  getComparison(first: string, second: string): Observable<any> {
    const url = `${this.url}/compareReleases/${first}:${second}`
    const body = {};
    return this.http.post(url, null)
    .pipe(catchError(this.handleError<any>('getComparison', [])));
  }

  // Insert release
  addRelease(release: JSON): Observable<any> {
    const url = `${this.url}/insert`;
    return this.http.post(url, release)
    .pipe(catchError(this.handleError<any>('addRelease')));
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
