import { NgModule } from '@angular/core';
import { Router, RouterModule, Routes, CanActivate } from '@angular/router';
import { CompsComponent } from './comps/comps.component';
import { InsertFileComponent } from './insert-file/insert-file.component';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';
import { ReleasesComponent } from './releases/releases.component';
import { AuthGuardService as AuthGuard } from './auth-guard.service';

const routes: Routes = [
  { path: 'modules/:name', component: ReleaseDetailComponent, canActivate: [AuthGuard] },
  { path: 'modules', component: ReleasesComponent, canActivate: [AuthGuard] },
  { path: 'components', component: CompsComponent, canActivate: [AuthGuard] },
  { path: 'insert', component: InsertFileComponent },
  { path: '', redirectTo: 'modules', pathMatch: 'full', canActivate: [AuthGuard] }
]

@NgModule({
  declarations: [],
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
