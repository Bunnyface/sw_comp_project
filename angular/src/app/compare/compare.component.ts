import { Component, OnInit } from '@angular/core';
import { Release } from '../release';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {

  releases: any = [];

  getReleases(): void {
    this.configService.getReleases().subscribe(releases => this.releases = releases);
  }

  getComparison(first: string, second: string): void {
    this.configService.getComparison(first, second).subscribe(res => console.log(res));
  }

  onSubmit(data) {
    console.log(data.first, data.second)
    const first = data.first;
    const second = data.second;
    this.getComparison(first, second);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getReleases();
  }

}
