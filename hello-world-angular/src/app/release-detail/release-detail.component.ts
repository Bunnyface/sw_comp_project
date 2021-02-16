import { Component, OnInit, Input } from '@angular/core';
import { Release } from '../release'

@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css']
})
export class ReleaseDetailComponent implements OnInit {

  @Input() release: Release;
  constructor() { }

  ngOnInit(): void {
  }

}
