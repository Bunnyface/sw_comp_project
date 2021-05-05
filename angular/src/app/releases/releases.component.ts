import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { ConfigService } from '../config.service';
import { SwCompManagerModule } from '../shared/module.model';


@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ReleasesComponent implements OnInit {

  modules: SwCompManagerModule[];
  columns: String[];
  dataSource = new MatTableDataSource<SwCompManagerModule>();
  expandedModule: SwCompManagerModule | null;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<any>;

  getModules(): void {
    this.configService.getFullModules().subscribe(res => {
      this.columns = Object.keys(res[0]);
      this.dataSource.data = res;
      this.dataSource.sort = this.sort;
      console.log(this.dataSource.data);
    });
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  constructor(private configService: ConfigService)  { }

  ngOnInit(): void {
    this.getModules();
  }
}
