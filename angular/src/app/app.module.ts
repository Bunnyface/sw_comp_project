import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule , FormsModule} from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatMenuModule } from '@angular/material/menu';
import { MatIcon, MatIconModule } from '@angular/material/icon'


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
import { InsertCompToModComponent } from './insert-comp-to-mod/insert-comp-to-mod.component';
import { CompsComponent } from './comps/comps.component';


@NgModule({
  declarations: [
    AppComponent,
    ReleasesComponent,
    ReleaseDetailComponent,
    DataInputComponent,
    CompareComponent,
    InsertModuleComponent,
    InsertComponentComponent,
    ComponentsComponent,
    InsertCompToModComponent,
    CompsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule, MatCheckboxModule,
    MatTableModule, MatSortModule,
    MatFormFieldModule, MatInputModule,
    MatCardModule, MatExpansionModule,
    MatListModule, MatPaginatorModule,
    MatMenuModule, MatIconModule
  ],
  providers: [],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
