import { Component, OnInit } from '@angular/core';
import { Release } from '../release';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {

  releases: Release[] = [];

  getReleases(): void {
    this.configService.getReleases().subscribe(releases => this.releases = releases);
  }

  onSubmit(data) {
    console.log(data.firstRelease, data.secondRelease)
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getReleases();
  }

}
