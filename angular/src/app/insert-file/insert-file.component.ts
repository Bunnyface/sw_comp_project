import { Component, OnInit } from '@angular/core';
import { ConfigService } from '../config.service';

@Component({
  selector: 'app-insert-file',
  templateUrl: './insert-file.component.html',
  styleUrls: ['./insert-file.component.css']
})
export class InsertFileComponent implements OnInit {

  fileName = "";
  body = {};

  // Read file contents
  onFileSelected(event): void {
    const file: File = event.target.files[0];
    if (file) {
      this.fileName = file.name;
      const reader = new FileReader();
      reader.onload = (event) => {
        this.body = JSON.parse(event.target.result as string);
      }
      reader.readAsText(file);
    }
  }

  // Delete selected file before uploading
  onDelete(): void {
    this.fileName = "";
    this.body = {};
  }

  // Submit selected file
  onSubmit(): void {
    this.addData(this.body);
  }

  addData(body) {
    const path = "insertMany";
    this.configService.insertFile(path, body, this.fileName).subscribe(res => {
      console.log(res);
      this.onDelete();
    });
  }

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
  }

}
