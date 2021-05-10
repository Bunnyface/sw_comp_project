import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { SwCompManagerModule } from './shared/module.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private url = 'http://localhost:8081';

  // Get all modules
  getModules(): Observable<any> {
    const url = `${this.url}/releases`;
    return this.http.post<any>(url, null)
      .pipe(catchError(this.handleError<String[]>('getModules', [])));
  }

  // Get full info on all modules
  getFullModules(): Observable<any> {
    const url = `${this.url}/moduleData`;
    return this.http.post<any>(url, null)
      .pipe(catchError(this.handleError<String[]>('getFullModules', [])));
  }

  // Get details of selected release
  getRelease(name: string): Observable<any> {
    const url = `${this.url}/releases/${name}`;
    return this.http.post(url, null)
      .pipe(catchError(this.handleError<any>(`getRelease id=${name}`)));
  }

  // Get comparison of two selected releases
  getComparison(body): Observable<any> {
    const url = `${this.url}/compare/`
    return this.http.post(url, body)
    .pipe(catchError(this.handleError<any>('getComparison', [])));
  }

  // Get all components
  getComponents(): Observable<any> {
    const url = `${this.url}/components`;
    return this.http.post<any>(url, null)
      .pipe(catchError(this.handleError<String[]>('getComponent', [])));
  }

  // Insert data
  insert(path: string, body: object): Observable<any> {
    const url = `${this.url}/${path}`;
    return this.http.put(url, body)
    .pipe(catchError(this.handleError<any>('insert')));
  }


  // Error handling
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.log(error); // log to console
      console.log(`${operation} failed: ${error.message}`); // user-friendly message
      return of(result as T); // return a safe value to keep app running
    }
  }

  constructor(private http: HttpClient) { }
}
