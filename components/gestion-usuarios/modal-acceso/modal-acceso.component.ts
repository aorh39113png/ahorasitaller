import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-acceso',
  templateUrl: './modal-acceso.component.html',
    standalone: false,

  styleUrls: ['./modal-acceso.component.css']
})
export class ModalAccesoComponent {
  @Input() modoEdicion = false;
  @Input() usuario: any = {};
  @Output() cerrar = new EventEmitter<void>();
  @Output() guardar = new EventEmitter<void>();
}
