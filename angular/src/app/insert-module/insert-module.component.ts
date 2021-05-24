import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { ConfigService } from '../config.service';
import { LoggerService } from '../logger.service';

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

  message: string;
  
  onSubmit(): void {
    this.addModule(this.module.value);
    this.module.reset();
  }

  addModule(body): void {
    const path = 'insertModule';
    const type = 'module'
    this.configService.insert(path, body, type).subscribe(res => {
      console.log(res)
      this.message = this.loggerService.messages[this.loggerService.messages.length -1]
    });
  }

  constructor(private configService: ConfigService, fb: FormBuilder, private loggerService: LoggerService) { 
    this.options = fb.group({
      subComponent: false,
      floatLabel: 'auto'
    });
  }

  ngOnInit(): void {
  }

}
