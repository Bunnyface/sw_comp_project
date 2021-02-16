import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
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
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }