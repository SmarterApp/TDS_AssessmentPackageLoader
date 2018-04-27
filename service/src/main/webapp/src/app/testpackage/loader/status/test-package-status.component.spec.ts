import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestPackageStatusComponent } from './test-package-status.component';

describe('TestPackageStatusComponent', () => {
  let component: TestPackageStatusComponent;
  let fixture: ComponentFixture<TestPackageStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestPackageStatusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestPackageStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
