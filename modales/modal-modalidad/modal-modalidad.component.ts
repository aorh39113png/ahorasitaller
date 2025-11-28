import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Modalidad } from '../../models/auth-response.model';

@Component({
  selector: 'app-modal-modalidad',
  templateUrl: './modal-modalidad.component.html',
  styleUrls: ['./modal-modalidad.component.css'],
  standalone: false
})
export class ModalModalidadComponent {
  @Input() set modalidadData(value: Modalidad) { this.modalidad = { ...value }; }
  @Input() modoEdicion = false;
  @Output() guardar = new EventEmitter<Modalidad>();
  @Output() cerrar = new EventEmitter<void>();
  public modalidad: Modalidad = { nombre: '' };
}