import { TestBed } from '@angular/core/testing';

import { Liability } from './liability';

describe('Liability', () => {
  let service: Liability;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Liability);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
