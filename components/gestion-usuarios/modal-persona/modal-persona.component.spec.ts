import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalPersonaComponent } from './modal-persona.component';

describe('ModalPersonaComponent', () => {
  let component: ModalPersonaComponent;
  let fixture: ComponentFixture<ModalPersonaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModalPersonaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalPersonaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
