import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertCompToModComponent } from './insert-comp-to-mod.component';

describe('InsertCompToModComponent', () => {
  let component: InsertCompToModComponent;
  let fixture: ComponentFixture<InsertCompToModComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertCompToModComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertCompToModComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
