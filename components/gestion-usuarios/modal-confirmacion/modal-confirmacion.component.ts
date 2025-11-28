import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-confirmacion',
  templateUrl: './modal-confirmacion.component.html',
    standalone: false,

  styleUrls: ['./modal-confirmacion.component.css']
})
export class ModalConfirmacionComponent {
  @Input() mensaje = '¿Está seguro?';
  @Output() aceptar = new EventEmitter<void>();
  @Output() cerrar = new EventEmitter<void>();
}
