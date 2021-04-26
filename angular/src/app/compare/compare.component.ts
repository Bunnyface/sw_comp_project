import { Component, OnInit } from '@angular/core';
import { Module } from '../module';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {

  modules: any = [];
  comparison: string;

  getModules(): void {
    this.configService.getModules().subscribe(res => this.modules = res);
  }

  getComparison(first: string, second: string): void {
    this.configService.getComparison(first, second).subscribe(res => this.comparison = JSON.stringify(res));
  }

  onSubmit(data) {
    const first = data.first;
    const second = data.second;
    this.getComparison(first, second);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getModules();
  }

}
