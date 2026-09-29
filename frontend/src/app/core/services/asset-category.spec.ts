import { TestBed } from '@angular/core/testing';

import { AssetCategory } from './asset-category';

describe('AssetCategory', () => {
  let service: AssetCategory;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssetCategory);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
