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
  selectedRelease: string;

  getReleases(): void {
    this.configService.getReleases().subscribe(res => this.releases = res);
  }

  getRelease(releaseName: string): void {
    this.configService.getRelease(releaseName).subscribe(res => this.selectedRelease = JSON.stringify(res));
  }

  onSubmit(data) {
    console.log("form submitted", data.release)
    this.getRelease(data.release);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getReleases();
  }
}
