import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertComponentComponent } from './insert-component.component';

describe('InsertComponentComponent', () => {
  let component: InsertComponentComponent;
  let fixture: ComponentFixture<InsertComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
