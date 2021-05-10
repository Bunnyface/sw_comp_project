import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ConfigService } from '../config.service';
import { SwCompManagerModule } from '../shared/module.model';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {

  @Input() modulesToCompare?: String[];
  same = new MatTableDataSource<any>();
  exFirst = new MatTableDataSource<any>();
  exSecond = new MatTableDataSource<any>();
  columns: String[] = ["name", "version"];
  @ViewChild(MatSort) sort: MatSort;

  getComparison(modulesToCompare): void {
    var body = {"first" : "", "second" : ""};
    body.first = modulesToCompare[0];
    body.second = modulesToCompare[1];

    this.configService.getComparison(body).subscribe(res => {
      this.same = res.same;
      this.exFirst = res.ex_first;
      this.exSecond = res.ex_second;
      this.same.sort = this.sort;
      this.exFirst.sort = this.sort;
      this.exSecond.sort = this.sort;
    });
  }


  constructor(private configService: ConfigService) { }

  ngOnInit(): void { 
    this.getComparison(this.modulesToCompare);
  }
}
