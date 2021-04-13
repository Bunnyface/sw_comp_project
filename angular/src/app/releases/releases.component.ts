import { Component, OnInit } from '@angular/core';
import { Release } from '../release';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css']
})
export class ReleasesComponent implements OnInit {

  releases: any = [];

  getReleases(): void {
    this.configService.getReleases().subscribe(res => this.releases = res);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getReleases();
  }
}
