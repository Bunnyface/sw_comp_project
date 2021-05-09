import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-data-input',
  templateUrl: './data-input.component.html',
  styleUrls: ['./data-input.component.css']
})
export class DataInputComponent implements OnInit {
  
  private files: File[] = []; 
  fileForm = new FormGroup({
    file: new FormControl('')
  })

  onFileSelected(event): void {
    this.files.push(event.target.files[0]);
  }

  onSubmit() {
    var data: string = '';
    var jsonData;
    if (this.files) {
      const reader = new FileReader();
      reader.onload = (event) => {
        data = event.target.result as string;
        jsonData = JSON.parse(data);
        this.addData(jsonData);
      }
      
      this.files.forEach(file => reader.readAsText(file));
      
    }
    this.fileForm.reset();
    this.files.splice(0, this.files.length);
  }

  addData(body): void {
    const path = ''; // TODO: add path when endpoint is ready
    // this.configService.insert(path, body).subscribe();
    console.log(body);
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
  }

}
