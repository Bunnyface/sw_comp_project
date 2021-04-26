import { NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';
import { CompareComponent } from './compare/compare.component';
import { DataInputComponent } from './data-input/data-input.component';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';
import { ReleasesComponent } from './releases/releases.component';

const routes: Routes = [
  { path: 'modules/:name', component: ReleaseDetailComponent },
  { path: 'modules', component: ReleasesComponent },
  { path: 'compare', component: CompareComponent },
  { path: 'insert', component: DataInputComponent },
  { path: '', redirectTo: 'modules', pathMatch: 'full' }
]

@NgModule({
  declarations: [],
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
