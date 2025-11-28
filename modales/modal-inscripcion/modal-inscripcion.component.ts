import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Estudiante, MapaActivo, Progra } from '../../models/auth-response.model';

@Component({
  selector: 'app-modal-inscripcion',
  standalone: false,
  templateUrl: './modal-inscripcion.component.html',
  styleUrl: './modal-inscripcion.component.css'
})
export class ModalInscripcionComponent {
  @Input() listaMapas: MapaActivo[] = [];
  @Input() listaEstudiantes: Estudiante[] = [];
  @Input() gestion: number = 0;
  @Input() modoEdicion = false;

  @Input() set inscripcionData(data: Progra | null) {
    if (data && this.modoEdicion) {
      this.codMapaSeleccionado = data.id.codmat + '|' + data.id.codpar;
      this.codpSeleccionado = data.id.codp;
    }
  }

  @Output() guardar = new EventEmitter<{codmat: string, codpar: number, codp: number}>();
  @Output() cerrar = new EventEmitter<void>();

  codMapaSeleccionado: string = "0";
  codpSeleccionado: number = 0;

  onGuardar(): void {
    const [codmat, codparStr] = this.codMapaSeleccionado.split('|');
    this.guardar.emit({ codmat, codpar: parseInt(codparStr), codp: this.codpSeleccionado });
  }
}
