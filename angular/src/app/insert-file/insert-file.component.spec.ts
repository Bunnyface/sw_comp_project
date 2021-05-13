import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertFileComponent } from './insert-file.component';

describe('InsertFileComponent', () => {
  let component: InsertFileComponent;
  let fixture: ComponentFixture<InsertFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertFileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
