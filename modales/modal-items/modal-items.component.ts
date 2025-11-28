import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Item } from '../../models/auth-response.model';

@Component({
  selector: 'app-modal-items',
  standalone: false,
  templateUrl: './modal-items.component.html',
  styleUrl: './modal-items.component.css'
})
export class ModalItemsComponent {
  @Input() set itemData(value: Item) { this.item = { ...value }; }
  @Input() modoEdicion = false;
  @Output() guardar = new EventEmitter<Item>();
  @Output() cerrar = new EventEmitter<void>();
  public item: Item = { nombre: '' };
}
