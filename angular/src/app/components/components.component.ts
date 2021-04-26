import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  styleUrls: ['./components.component.css']
})
export class ComponentsComponent implements OnInit {

  @Input() module_id;
  components: JSON;

  newComponent = new FormGroup({
    comp_id: new FormControl(''),
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
  addComponent(component) {
    const path = "module_component";
    const body = this.formatBody(component);
    this.configService.insert(path, body).subscribe();
  }

  // Format the request body to fit insert endpoint
  formatBody(component): object {
    var columns = ["module_id"];
    var data = [this.module_id.toString()];
    Object.keys(component).map((value) => columns.push(value));
    Object.values(component).map((value) => data.push(value));
    var body = { "columns": [], "data": [[]] };
    body.columns = columns;
    body.data[0] = data;
    return body;
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
    this.getComponents();
  }

}
