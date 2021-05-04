import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';
import { SwCompManagerComponent } from '../shared/component.model';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  styleUrls: ['./components.component.css']
})
export class ComponentsComponent implements OnInit {

  @Input() modulename;
  components: Array<SwCompManagerComponent>;

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
    console.log(body);
    this.configService.insert(path, body).subscribe();
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getComponents();
  }

}
