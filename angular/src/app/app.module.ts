import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule , FormsModule} from '@angular/forms';

import { AppComponent } from './app.component';
import { ReleasesComponent } from './releases/releases.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';

import { DataInputComponent } from './data-input/data-input.component';
import { CompareComponent } from './compare/compare.component';
import { AppRoutingModule } from './app-routing.module';
import { InsertModuleComponent } from './insert-module/insert-module.component';
import { InsertComponentComponent } from './insert-component/insert-component.component';
import { ComponentsComponent } from './components/components.component';

@NgModule({
  declarations: [
    AppComponent,
    ReleasesComponent,
    ReleaseDetailComponent,
    DataInputComponent,
    CompareComponent,
    InsertModuleComponent,
    InsertComponentComponent,
    ComponentsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
