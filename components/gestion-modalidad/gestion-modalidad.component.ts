import { Component, OnInit } from '@angular/core';
import { ModalidadService } from '../../servicios/modalidad.service';
import { NotificationService } from '../../servicios/notification.service';
import { Modalidad, PageResponse } from '../../models/auth-response.model';

@Component({
  selector: 'app-gestion-modalidad',
  templateUrl: './gestion-modalidad.component.html',
  styleUrls: ['./gestion-modalidad.component.css'],
  standalone: false
})
export class GestionModalidadComponent implements OnInit {
  modalidades: Modalidad[] = [];
  filtro = '';
  filtroEstado = 'TODOS';

  paginaActual: number = 1;
  itemsPorPagina: number = 10;
  totalPaginas: number = 0;

  modalVisible = false;
  modalConfirmVisible = false;
  modoEdicion = false;
  mensajeConfirmacion = '';
  modalidadSeleccionada: Modalidad | null = null;
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

  constructor(
    private modalidadService: ModalidadService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void { this.cargarModalidades(); }

  cargarModalidades(): void {
    if (isNaN(this.paginaActual) || this.paginaActual < 1) { this.paginaActual = 1; }
    this.modalidadService.listarPaginado(this.filtro, this.filtroEstado, this.paginaActual, this.itemsPorPagina)
      .subscribe({
        next: (res) => { this.modalidades = res.content; this.totalPaginas = res.totalPages; this.paginaActual = res.number + 1; },
        error: (err) => console.error('Error al cargar modalidades:', err)
      });
  }

  onFiltroChange() { this.paginaActual = 1; this.cargarModalidades(); }
  onEstadoChange() { this.paginaActual = 1; this.cargarModalidades(); }
  cambiarPagina(page: number) { if (page >= 1 && page <= this.totalPaginas) { this.paginaActual = page; this.cargarModalidades(); } }

  abrirModalNuevo() { this.modoEdicion = false; this.modalidadSeleccionada = { nombre: '' }; this.modalVisible = true; }
  abrirModalEditar(m: Modalidad) { this.modoEdicion = true; this.modalidadSeleccionada = m; this.modalVisible = true; }

  guardarModalidad(m: Modalidad) {
    this.modalidadService.crear(m).subscribe({
      next: () => {
        this.paginaActual = 1;
        this.cargarModalidades();
        this.cerrarModal();
        this.notificationService.showSuccess('Modalidad creada con éxito');
      },
      error: (err) => {
        console.error('Error al crear:', err);
        if (err.error && err.error.message && err.error.message.includes('ya existe')) {
          this.notificationService.showError(err.error.message);
        } else {
          this.notificationService.showError('Error al crear la modalidad');
        }
      }
    });
  }

  modificarModalidad(m: Modalidad) {
    if (!m.codm) return;
    this.modalidadService.modificar(m.codm, m).subscribe({
      next: () => {
        this.cargarModalidades();
        this.cerrarModal();
        this.notificationService.showSuccess('Modalidad actualizada con éxito');
      },
      error: (err) => {
        console.error('Error al modificar:', err);
        if (err.error && err.error.message && err.error.message.includes('ya existe')) {
          this.notificationService.showError(err.error.message);
        } else {
          this.notificationService.showError('Error al actualizar la modalidad');
        }
      }
    });
  }

  confirmarEliminar(m: Modalidad) { this.tipoConfirmacion = 'eliminar'; this.modalidadSeleccionada = m; this.mensajeConfirmacion = '¿Seguro de Eliminar la Modalidad?'; this.modalConfirmVisible = true; }
  confirmarHabilitar(m: Modalidad) { this.tipoConfirmacion = 'habilitar'; this.modalidadSeleccionada = m; this.mensajeConfirmacion = '¿Seguro de Habilitar la Modalidad?'; this.modalConfirmVisible = true; }

  ejecutarConfirmacion() {
    if (!this.modalidadSeleccionada?.codm) return;
    const id = this.modalidadSeleccionada.codm;
    const obs = this.tipoConfirmacion === 'eliminar' ? this.modalidadService.eliminar(id) : this.modalidadService.habilitar(id);
    obs.subscribe({
      next: () => {
        this.cargarModalidades();
        this.cerrarModalConfirmacion();
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminada' : 'habilitada';
        this.notificationService.showSuccess(`Modalidad ${accion} con éxito`);
      },
      error: (err) => {
        console.error('Error estado:', err);
        this.notificationService.showError('Error al cambiar estado de la modalidad');
      }
    });
  }

  cerrarModal() { this.modalVisible = false; this.modalidadSeleccionada = null; }
  cerrarModalConfirmacion() { this.modalConfirmVisible = false; this.modalidadSeleccionada = null; }
}