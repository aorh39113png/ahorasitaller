import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Role } from '../../models/auth-response.model';

@Component({
  selector: 'app-modal-rol',
  templateUrl: './modalrol.component.html',
  styleUrls: ['./modalrol.component.css'],
   standalone: false
})
export class ModalRolComponent {
  // Cuando editamos, recibimos el rol.
  // Usamos un setter para clonar el objeto y evitar mutaciones (two-way binding accidental).
  @Input() set rolData(value: Role) {
    this.rol = { ...value }; // Clonamos
  }
  @Input() modoEdicion = false;

  @Output() guardar = new EventEmitter<Role>();
  @Output() cerrar = new EventEmitter<void>();

  // Objeto local para el formulario
  public rol: Role = { nombre: '' };

  onGuardar() {
    this.guardar.emit(this.rol);
  }
}
