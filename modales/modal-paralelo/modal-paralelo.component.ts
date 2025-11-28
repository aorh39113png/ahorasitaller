import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Paralelo } from '../../models/auth-response.model'; // Ajusta la ruta si es necesario

@Component({
  selector: 'app-modal-paralelo',
  standalone: false,
  templateUrl: './modal-paralelo.component.html',
  styleUrls: ['./modal-paralelo.component.css']
})
export class ModalParaleloComponent {

  // Usamos un 'setter' para clonar el objeto y evitar mutaciones
  @Input() set paraleloData(value: Paralelo) {
    this.paralelo = { ...value };
  }
  @Input() modoEdicion = false;

  @Output() guardar = new EventEmitter<Paralelo>();
  @Output() cerrar = new EventEmitter<void>();

  // Objeto local para el formulario
  public paralelo: Paralelo = { nombre: '' };
}
