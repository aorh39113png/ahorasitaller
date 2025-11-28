import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Nivel } from '../../models/auth-response.model'; // Ajusta la ruta si es necesario

@Component({
  selector: 'app-modal-nivel',
  standalone:false,
  templateUrl: './modal-nivel.component.html',
  styleUrls: ['./modal-nivel.component.css']
})
export class ModalNivelComponent {

  // Usamos un 'setter' para clonar el objeto y evitar mutaciones
  @Input() set nivelData(value: Nivel) {
    this.nivel = { ...value };
  }
  @Input() modoEdicion = false;

  @Output() guardar = new EventEmitter<Nivel>();
  @Output() cerrar = new EventEmitter<void>();

  // Objeto local para el formulario
  public nivel: Nivel = { nombre: '' };
}
