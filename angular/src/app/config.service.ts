import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, retry, map, tap } from 'rxjs/operators';

import { SwCompManagerModule } from './shared/module.model';
import { LoggerService } from './logger.service';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private url = 'http://localhost:8081';

  // Get all modules
  getModules(): Observable<any> {
    const url = `${this.url}/releases`;
    return this.http.post<any>(url, null)
      .pipe(
        tap(_ => this.log('fetched modules')),
        catchError(this.handleError<String[]>('getModules', [])));
  }

  // Get full info on all modules
  getFullModules(): Observable<any> {
    const url = `${this.url}/moduleData`;
    return this.http.post<any>(url, null)
      .pipe(
        tap(_ => this.log('fetched modules')),
        catchError(this.handleError<String[]>('getFullModules', [])));
  }

  // Get details of selected release
  getRelease(name: string): Observable<any> {
    const url = `${this.url}/releases/${name}`;
    return this.http.post(url, null)
      .pipe(
        tap(_ => this.log(`fetched details of module with name ${name}`)),
        catchError(this.handleError<any>(`getRelease id=${name}`)));
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
      .pipe(
        tap((_) => this.log('fetched components')),
        catchError(this.handleError<String[]>('getComponent', [])));
  }

  // Insert data
  insert(path: string, body: object, type: string): Observable<any> {
    const url = `${this.url}/${path}`;
    return this.http.put(url, body)
    .pipe(
      tap((newItem) => this.log(`added ${type} with name ${newItem[0].name}`)),
      catchError(this.handleError<any>('insert')));
  }

  insertFile(path: string, body: object, fileName: string): Observable<any> {
    const url = `${this.url}/${path}`;
    return this.http.put(url, body)
    .pipe(
      tap((newItem) => this.log(`added file with name ${fileName}`)),
      catchError(this.handleError<any>('insertFile')));
  }

  insertCompToMod(path: string, body: object): Observable<any> {
    const url = `${this.url}/${path}`;
    return this.http.put(url, body)
    .pipe(
      tap((newItem) => this.log(`added component with id ${newItem[0].comp_id} to module with id ${newItem[0].module_id}`)),
      catchError(this.handleError<any>('insertCompToMod')));
  }


  // Delete data
  delete(path: String, type: string): Observable<any> {
    const url = `${this.url}/${path}`;
    return this.http.delete(url)
    .pipe(
      tap((newItem) => this.log(`deleted ${type} with name ${newItem[0].name}`)),
      catchError(this.handleError<any>('delete')));
  }

  private log(message: string) {
    this.loggerService.add(message);
  }

  // Error handling
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.log(error);
      this.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    }
  }

  constructor(private http: HttpClient, private loggerService: LoggerService) { }
}
