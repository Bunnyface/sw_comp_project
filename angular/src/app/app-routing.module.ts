import { NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';
import { CompsComponent } from './comps/comps.component';
import { InsertComponentComponent } from './insert-component/insert-component.component';
import { InsertFileComponent } from './insert-file/insert-file.component';
import { InsertModuleComponent } from './insert-module/insert-module.component';
import { LogComponent } from './log/log.component';
import { ReleaseDetailComponent } from './release-detail/release-detail.component';
import { ReleasesComponent } from './releases/releases.component';

const routes: Routes = [
  { path: 'modules/:name', component: ReleaseDetailComponent },
  { path: 'insert-module', component: InsertModuleComponent },
  { path: 'modules', component: ReleasesComponent },
  { path: 'insert-component', component: InsertComponentComponent },
  { path: 'components', component: CompsComponent },
  { path: 'insert', component: InsertFileComponent },
  { path: 'log', component: LogComponent },
  { path: '', redirectTo: 'modules', pathMatch: 'full' }
]

@NgModule({
  declarations: [],
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
