import { Component, OnInit } from '@angular/core';
import { Module } from '../module';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css']
})
export class ReleasesComponent implements OnInit {

  modules: any = [];

  getModules(): void {
    this.configService.getModules().subscribe(res => this.modules = res);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getModules();
  }
}
