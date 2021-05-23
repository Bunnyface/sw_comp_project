import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';
import { LoggerService } from '../logger.service';
import { SwCompManagerComponent } from '../shared/component.model';

@Component({
  selector: 'app-insert-comp-to-mod',
  templateUrl: './insert-comp-to-mod.component.html',
  styleUrls: ['./insert-comp-to-mod.component.css']
})
export class InsertCompToModComponent implements OnInit {

  message: string;
  @Input() modulename;
  components: Array<SwCompManagerComponent>;

  options: FormGroup;
  newComponent = new FormGroup({
    componentname: new FormControl(''),
    usage_type: new FormControl(''),
    attr_value1: new FormControl(''),
    attr_value2: new FormControl(''),
    attr_value3: new FormControl(''),
    date: new FormControl(''),
    comment_one: new FormControl(''),
    comment_two: new FormControl('')
  });

  getComponents(): void {
    this.configService.getComponents().subscribe(res => this.components = res);
  }

  onSubmit() {
    this.addComponent(this.newComponent.value);
    this.newComponent.reset();
  }

  // Add selected component to module
  addComponent(body) {
    const path = "insertComponentToModule";
    body.modulename = this.modulename;
    this.configService.insertCompToMod(path, body).subscribe(res => {
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
    this.getComponents();
  }

}
