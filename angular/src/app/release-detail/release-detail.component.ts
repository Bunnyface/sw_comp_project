import { Component, OnInit, Input } from '@angular/core';
import { Release } from '../release'
import { ConfigService } from '../config.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';


@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css']
})
export class ReleaseDetailComponent implements OnInit {

  release: Release;

  constructor(
    private route: ActivatedRoute,
    private configService: ConfigService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getRelease();
  }

  getRelease(): void {
    const name = this.route.snapshot.paramMap.get('name');
    this.configService.getRelease(name).subscribe(res => this.release = res)
  }

  goBack(): void {
    this.location.back();
  }
}
