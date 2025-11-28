import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Menu } from '../../models/auth-response.model';

@Component({
  selector: 'app-modal-menu',
  templateUrl: './modal-menu.component.html',
  styleUrls: ['./modal-menu.component.css'],
  standalone:false
})
export class ModalMenuComponent {

  // Usamos un setter para clonar el objeto y evitar mutaciones
  @Input() set menuData(value: Menu) {
    this.menu = { ...value };
  }
  @Input() modoEdicion = false;

  @Output() guardar = new EventEmitter<Menu>();
  @Output() cerrar = new EventEmitter<void>();

  // Objeto local para el formulario
  public menu: Menu = { nombre: '' };
}
