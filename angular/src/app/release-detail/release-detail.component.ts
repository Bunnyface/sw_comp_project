import { Component, OnInit, Input } from '@angular/core';
import { Module } from '../module'
import { ConfigService } from '../config.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';


@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css']
})
export class ReleaseDetailComponent implements OnInit {

  module: Module;
  edit: boolean = false

  constructor(
    private route: ActivatedRoute,
    private configService: ConfigService,
    private location: Location
  ) { }

  getRelease(): void {
    const name = this.route.snapshot.paramMap.get('name');
    this.configService.getRelease(name).subscribe(res => this.module = res)
  }

  onEdit(): void {
    if (this.edit) {
      this.edit = false;
    }
    else {
      this.edit = true;
    }
  }

  goBack(): void {
    this.location.back();
  }

  ngOnInit(): void {
    this.getRelease();
  }

}
