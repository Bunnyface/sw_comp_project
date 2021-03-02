import { NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';
import { CompareComponent } from './compare/compare.component';
import { DataInputComponent } from './data-input/data-input.component';
import { ReleasesComponent } from './releases/releases.component';

const routes: Routes = [
  { path: 'releases', component: ReleasesComponent },
  { path: 'compare', component: CompareComponent },
  { path: 'insert', component: DataInputComponent }
]

@NgModule({
  declarations: [],
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
