import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';
import { LoggerService } from '../logger.service';

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

  message: string;

  onSubmit(): void {
    this.addComponent(this.component.value);
    this.component.reset();
  }

  addComponent(body) {
    var path: string;
    var type: string;
    this.options.value.subComponent ? path = 'insertSubComponent' : path = 'insertComponent';
    this.options.value.subComponent ? type = 'sub-component' : type = 'component';
    this.configService.insert(path, body, type).subscribe(_ => this.message = this.loggerService.messages[this.loggerService.messages.length -1]);
    
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
