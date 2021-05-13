import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-insert-component',
  templateUrl: './insert-component.component.html',
  styleUrls: ['./insert-component.component.css']
})
export class InsertComponentComponent implements OnInit {

  options: FormGroup;

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
    var path: string;
    this.options.value.subComponent ? path = 'insertSubComponent' : path = 'insertComponent';
    this.configService.insert(path, body).subscribe();
    console.log(this.options.value.subComponent, body);
  }

  constructor(private configService: ConfigService, fb: FormBuilder) { 
    this.options = fb.group({
      subComponent: false,
      floatLabel: 'auto'
    });
  }

  ngOnInit(): void {
  }

}
