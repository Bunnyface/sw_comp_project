import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { ConfigService } from '../config.service';
import { LoggerService } from '../logger.service';
import { SwCompManagerComponent } from '../shared/component.model';

@Component({
  selector: 'app-comps',
  templateUrl: './comps.component.html',
  styleUrls: ['./comps.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class CompsComponent implements OnInit {
  components: SwCompManagerComponent[];
  message: string;

  columns: String[] = ["name", "version", "url", "license", "copyright", "actions"];
  dataSource = new MatTableDataSource<SwCompManagerComponent>();
  expandedComponent: SwCompManagerComponent | null;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<any>;

  getComponents(): void {
    this.configService.getComponents().subscribe(res => {
      this.dataSource.data = res;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  onDelete(name, version): void {
    const path = `delete/component/${name}:${version}`;
    const type = 'component';
    this.configService.delete(path, type).subscribe(_ => {
      this.message = this.loggerService.messages[this.loggerService.messages.length -1]
    });
    this.getComponents();
  }

  constructor(private configService: ConfigService, private loggerService: LoggerService)  { }

  ngOnInit(): void {
    this.getComponents();
  }
}
