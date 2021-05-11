import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-insert-module',
  templateUrl: './insert-module.component.html',
  styleUrls: ['./insert-module.component.css']
})
export class InsertModuleComponent implements OnInit {

  options: FormGroup;

  module = new FormGroup({
    name: new FormControl('')
  });

  onSubmit(): void {
    this.addModule(this.module.value);
    this.module.reset();
  }

  addModule(body): void {
    const path = 'insertModule';
    this.configService.insert(path, body).subscribe();
    console.log(body);
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
