import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Materia, Nivel } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
  selector: 'app-modal-materia',
  standalone:false,
  templateUrl: './modal-materia.component.html',
  styleUrls: ['./modal-materia.component.css']
})
export class ModalMateriaComponent {

  // Recibe la lista de niveles desde el componente padre (GestionMaterias)
  @Input() niveles: Nivel[] = [];

  // Usamos un 'setter' para clonar el objeto
  @Input() set materiaData(value: Materia) {
    // Clonamos para evitar mutaciones accidentales
    this.materia = JSON.parse(JSON.stringify(value));
  }
  @Input() modoEdicion = false;

  @Output() guardar = new EventEmitter<Materia>();
  @Output() cerrar = new EventEmitter<void>();

  // Objeto local para el formulario
  public materia: Materia = {
    codmat: '',
    nombre: '',
    nivel: { codn: 0 } // Estado inicial (0 = '-- Seleccione --')
  };
}
