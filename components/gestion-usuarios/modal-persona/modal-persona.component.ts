import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-persona',
  standalone: false,
  templateUrl: './modal-persona.component.html',
  styleUrls: ['./modal-persona.component.css']
})
export class ModalPersonaComponent {
  @Input() modoEdicion = false;
  @Input() persona: any = {};
  @Output() cerrar = new EventEmitter<void>();
  @Output() guardar = new EventEmitter<void>();
  @Output() fileChange = new EventEmitter<any>();
  @Output() removePhoto = new EventEmitter<void>();

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.fileChange.emit(file);
    }
  }
}
