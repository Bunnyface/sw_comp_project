import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
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

  columns: String[] = ["name", "components"];
  dataSource = new MatTableDataSource<SwCompManagerModule>();
  expandedModule: SwCompManagerModule | null;

  addModuleMode = false;
  compareMode = false;
  compareSubmitted = false;
  disabled = false;
  modulesToCompare: String[] = [];
  selection = new SelectionModel<SwCompManagerModule>(true, []);

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<any>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  // Get all modules and set them to table
  getModules(): void {
    this.configService.getFullModules().subscribe(res => {
      this.dataSource.data = res;
      this.dataSource.sort = this.sort;
    });
  }

  // Search bar behaviour
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  // Set compare mode on to select modules to compare
  onCompareMode(cancel: Boolean): void {
    if (cancel) {
      this.compareMode = false;
      this.compareSubmitted = false;
      this.columns.shift();
    }
    else {
      this.compareMode = true;
    this.columns.unshift("select");
    }
  }

  // When compare selection is submitted
  onCompareSubmit(): void {
    this.compareSubmitted = true;
  }

  // Set module addition mode on
  onAddModule(): void {
    this.addModuleMode = true;
  }

  // Select modules to compare
  onSelect(row: SwCompManagerModule) {
    `${this.selection.isSelected(row) ? this.modulesToCompare.splice(this.modulesToCompare.indexOf(row.name), 1) : this.modulesToCompare.push(row.name)}`;
    if (this.modulesToCompare.length >= 2) {
      this.disabled = true;
    }
    else {
      this.disabled = false;
    }
  }

  // Go back to modules view from compare view
  onCancel(): void {
    this.compareSubmitted = false;
    this.onCompareMode(true);
  }

  constructor(private configService: ConfigService)  { }

  ngOnInit(): void {
    this.getModules();
  }
}
