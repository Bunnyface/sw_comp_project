import { Component, OnInit } from '@angular/core';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-data-input',
  templateUrl: './data-input.component.html',
  styleUrls: ['./data-input.component.css']
})
export class DataInputComponent implements OnInit {
  
  public file: File | null = null;

  // Read the contents of the file and add data to database.
  onFileSelected(event): void {

    this.file = event.target.files[0];
    var data: string = '';
    var jsonData;

    if (this.file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        data = event.target.result as string;
        console.log(data);
        jsonData = JSON.parse(data)
        this.configService.addRelease(jsonData).subscribe(res => console.log(res));
      }
      reader.readAsText(this.file);
    }

  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
  }

}
