import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Liabilities } from './liabilities';

describe('Liabilities', () => {
  let component: Liabilities;
  let fixture: ComponentFixture<Liabilities>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Liabilities]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Liabilities);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
