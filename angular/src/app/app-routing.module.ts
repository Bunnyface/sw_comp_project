import { NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';
import { CompsComponent } from './comps/comps.component';
import { InsertFileComponent } from './insert-file/insert-file.component';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';
import { ReleasesComponent } from './releases/releases.component';

const routes: Routes = [
  { path: 'modules/:name', component: ReleaseDetailComponent },
  { path: 'modules', component: ReleasesComponent },
  { path: 'components', component: CompsComponent },
  { path: 'insert', component: InsertFileComponent },
  { path: '', redirectTo: 'modules', pathMatch: 'full' }
]

@NgModule({
  declarations: [],
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
