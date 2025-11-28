import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAccesoComponent } from './modal-acceso.component';

describe('ModalAccesoComponent', () => {
  let component: ModalAccesoComponent;
  let fixture: ComponentFixture<ModalAccesoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModalAccesoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalAccesoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
