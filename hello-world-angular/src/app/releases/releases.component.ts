import { Component, OnInit } from '@angular/core';
import { Release } from '../release';
import { RELEASES } from '../mock-releases';

@Component({
  selector: 'app-releases',
  templateUrl: './releases.component.html',
  styleUrls: ['./releases.component.css']
})
export class ReleasesComponent implements OnInit {

  releases = RELEASES;

  selectedRelease: Release;

  onSubmit(data) {
    console.log("form submitted", data.releaseId)
    this.selectedRelease = this.releases[data.releaseId];
  }

  constructor() { }

  ngOnInit(): void {
  }
}
