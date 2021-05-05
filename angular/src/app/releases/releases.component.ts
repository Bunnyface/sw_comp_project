import { Component, OnInit } from '@angular/core';
import { ConfigService } from '../config.service';
import { SwCompManagerModule } from '../shared/module.model';

@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css']
})
export class ReleasesComponent implements OnInit {

  modules: Array<SwCompManagerModule>;

  getModules(): void {
    this.configService.getModules().subscribe(res => this.modules = res);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getModules();
  }
}
