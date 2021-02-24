import { Component, OnInit } from '@angular/core';
import { Release } from '../release';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css']
})
export class ReleasesComponent implements OnInit {

  releases: Release[] = [];

  selectedRelease: Release;

  getReleases(): void {
    this.configService.getReleases().subscribe(releases => this.releases = releases);
  }

  onSubmit(data) {
    console.log("form submitted", data.releaseId)
    this.selectedRelease = this.releases[data.releaseId];
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getReleases();
  }
}
