import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-insert-component',
  templateUrl: './insert-component.component.html',
  styleUrls: ['./insert-component.component.css']
})
export class InsertComponentComponent implements OnInit {

  subComponent: boolean = false;

  component = new FormGroup({
    name: new FormControl(''),
    url: new FormControl(''),
    version: new FormControl(''),
    license: new FormControl(''),
    copyright: new FormControl('')
  });

  onSubmit(): void {
    this.addComponent(this.component.value);
    this.component.reset();
  }

  addComponent(body) {
    var path = '';
    this.subComponent ? path = '' : path = ''; // TODO: add path when endpoint is ready
    // this.configService.insert(path, component).subscribe();
    console.log(this.subComponent, body);
  }

  onChange(isChecked: boolean): void {
    if (isChecked) {
      this.subComponent = true;
    }
    else {
      this.subComponent = false;
    }
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
  }

}
