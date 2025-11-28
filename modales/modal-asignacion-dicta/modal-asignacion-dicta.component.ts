import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MapaActivo, Profesor, Dicta } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
  selector: 'app-modal-asignacion-dicta',
  standalone:false,
  templateUrl: './modal-asignacion-dicta.component.html',
  styleUrls: ['./modal-asignacion-dicta.component.css']
})
export class ModalAsignacionDictaComponent {

  // --- DATOS PARA LLENAR DROPDOWNS ---
  @Input() listaMapas: MapaActivo[] = [];
  @Input() listaProfesores: Profesor[] = [];
  @Input() gestion: number = 0;
  @Input() modoEdicion = false;

  @Input() set asignacionData(data: Dicta | null) {
    if (data && this.modoEdicion) {
      // Si estamos en modo edición, pre-seleccionamos los valores
      this.codMapaSeleccionado = data.id.codmat + '|' + data.id.codpar;
      this.codpSeleccionado = data.id.codp;
    }
  }

  @Output() guardar = new EventEmitter<{codmat: string, codpar: number, codp: number}>();
  @Output() cerrar = new EventEmitter<void>();

  // --- IDs seleccionados en los dropdowns ---
  // Usamos "0" como valor inicial para "-- Seleccione --"
  codMapaSeleccionado: string = "0"; // "codmat|codpar"
  codpSeleccionado: number = 0;

  onGuardar(): void {
    // Dividimos el valor "codmat|codpar"
    const [codmat, codparStr] = this.codMapaSeleccionado.split('|');
    const codpar = parseInt(codparStr, 10);
    const codp = this.codpSeleccionado;

    this.guardar.emit({ codmat, codpar, codp });
  }
}
