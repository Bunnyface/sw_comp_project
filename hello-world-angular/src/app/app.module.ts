import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
<<<<<<< HEAD
<<<<<<< HEAD
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { ReleasesComponent } from './releases/releases.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    ReleasesComponent,
    ReleaseDetailComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    FormsModule
=======
=======
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
>>>>>>> create dropdown that shows mock data

import { AppComponent } from './app.component';
import { ReleasesComponent } from './releases/releases.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    ReleasesComponent,
    ReleaseDetailComponent
  ],
  imports: [
<<<<<<< HEAD
    BrowserModule
>>>>>>> initialize angular portion of project
=======
    BrowserModule,
    HttpClientModule,
    NgbModule,
    FormsModule
>>>>>>> create dropdown that shows mock data
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
