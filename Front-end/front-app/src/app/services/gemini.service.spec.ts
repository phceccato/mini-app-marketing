import { TestBed } from '@angular/core/testing';

import { GeminiService } from '../services/gemini.service';

describe('GeminiService', () => {
  let service: GeminiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GeminiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
