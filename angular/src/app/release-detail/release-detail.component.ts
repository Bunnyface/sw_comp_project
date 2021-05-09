import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ConfigService } from '../config.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { SwCompManagerModule } from '../shared/module.model';
import { SwCompManagerComponent } from '../shared/component.model';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { animate, state, style, transition, trigger } from '@angular/animations';


@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ReleaseDetailComponent implements OnInit {

  module: SwCompManagerModule;
  components: SwCompManagerComponent[];
  edit: boolean = false;
  
  columns: String[] = ["name", "version", "url", "license", "copyright", "usage_type", "date"];
  dataSource = new MatTableDataSource<SwCompManagerComponent>();
  expandedComponent: SwCompManagerComponent | null;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<any>;

  constructor(
    private route: ActivatedRoute,
    private configService: ConfigService,
    private location: Location
  ) { }

  getRelease(): void {
    const name = this.route.snapshot.paramMap.get('name');
    this.configService.getRelease(name).subscribe(res => {
      this.module = res;
      // this.columns = Object.keys(res.components[0]);
      this.dataSource.data = res.components;
      this.dataSource.sort = this.sort;
      console.log(this.dataSource.data);
    });
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  onEdit(): void {
    if (this.edit) {
      this.edit = false;
    }
    else {
      this.edit = true;
    }
  }

  goBack(): void {
    this.location.back();
  }

  ngOnInit(): void {
    this.getRelease();
  }

}
