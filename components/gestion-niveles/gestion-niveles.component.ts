import { Component, OnInit } from '@angular/core';
import { NivelesService } from '../../servicios/niveles.service'; // Ajusta la ruta
import { NotificationService } from '../../servicios/notification.service';
import { Nivel, PageResponse } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
  selector: 'app-gestion-niveles',
  standalone: false,
  templateUrl: './gestion-niveles.component.html',
  styleUrls: ['./gestion-niveles.component.css']
})
export class GestionNivelesComponent implements OnInit {

  niveles: Nivel[] = [];

  // FILTROS
  filtro = '';
  filtroEstado = 'TODOS'; // "TODOS", "ACTIVOS", "BAJAS"

  // PAGINACIÓN
  paginaActual = 1;
  itemsPorPagina = 10; // B-13 pide 10 elementos por página [cite: 1115]
  totalPaginas = 0;

  // CONTROL DE MODALES
  modalNivelVisible = false;
  modalConfirmVisible = false;
  modoEdicion = false;

  mensajeConfirmacion = '';
  nivelSeleccionado: Nivel | null = null;
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

  constructor(
    private nivelesService: NivelesService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.cargarNiveles();
  }

  /**
   * Carga la lista de niveles desde el backend (paginada y filtrada)
   */
  cargarNiveles(): void {
    this.nivelesService.listarPaginado(
      this.filtro,
      this.filtroEstado,
      this.paginaActual,
      this.itemsPorPagina
    ).subscribe({
      next: (response: PageResponse<Nivel>) => {
        this.niveles = response.content;
        this.totalPaginas = response.totalPages;
        this.paginaActual = response.number + 1; // Spring es 0-based
      },
      error: (err) => console.error('Error al cargar niveles:', err)
    });
  }

  // --- Disparadores de recarga ---

  onFiltroChange(): void {
    this.paginaActual = 1;
    this.cargarNiveles();
  }

  onEstadoChange(): void {
    this.paginaActual = 1;
    this.cargarNiveles();
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
      this.cargarNiveles();
    }
  }

  // --- CRUD (Modales) ---

  // B-13.1. Adicionar Nuevo Nivel
  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.nivelSeleccionado = { nombre: '' };
    this.modalNivelVisible = true;
  }

  guardarNivel(nivel: Nivel): void {
    this.nivelesService.crear(nivel).subscribe({
      next: () => {
        this.notificationService.showSuccess('Nivel creado correctamente');
        this.cargarNiveles();
        this.cerrarModalNivel();
      },
      error: (err) => {
        console.error('Error al crear nivel:', err);
        this.notificationService.showError('Error al crear nivel');
      }
    });
  }

  // B-13.2. Modificar Nivel
  abrirModalEditar(nivel: Nivel): void {
    this.modoEdicion = true;
    this.nivelSeleccionado = nivel;
    this.modalNivelVisible = true;
  }

  modificarNivel(nivel: Nivel): void {
    if (!nivel.codn) return;
    this.nivelesService.modificar(nivel.codn, nivel).subscribe({
      next: () => {
        this.notificationService.showSuccess('Nivel modificado correctamente');
        this.cargarNiveles();
        this.cerrarModalNivel();
      },
      error: (err) => {
        console.error('Error al modificar nivel:', err);
        this.notificationService.showError('Error al modificar nivel');
      }
    });
  }

  // B-13.3. Eliminar Nivel
  confirmarEliminar(nivel: Nivel): void {
    this.tipoConfirmacion = 'eliminar';
    this.nivelSeleccionado = nivel;
    this.mensajeConfirmacion = '¿Seguro de Eliminar Nivel?'; // [cite: 1166]
    this.modalConfirmVisible = true;
  }

  // B-13.4. Habilitar Nivel
  confirmarHabilitar(nivel: Nivel): void {
    this.tipoConfirmacion = 'habilitar';
    this.nivelSeleccionado = nivel;
    this.mensajeConfirmacion = '¿Seguro de Habilitar Nivel?'; // [cite: 1176]
    this.modalConfirmVisible = true;
  }

  /**
   * Ejecuta la acción (Eliminar o Habilitar) confirmada en el modal
   */
  ejecutarConfirmacion(): void {
    if (!this.nivelSeleccionado?.codn) return;

    const id = this.nivelSeleccionado.codn;
    const obs =
      this.tipoConfirmacion === 'eliminar'
        ? this.nivelesService.eliminar(id)
        : this.nivelesService.habilitar(id);

    obs.subscribe({
      next: () => {
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminado' : 'habilitado';
        this.notificationService.showSuccess(`Nivel ${accion} correctamente`);
        this.cargarNiveles();
        this.cerrarModalConfirmacion();
      },
      error: (err) => {
        console.error('Error al actualizar estado del nivel:', err);
        this.notificationService.showError('Error al actualizar estado del nivel');
      },
    });
  }

  // --- Control de Cierre de Modales ---

  cerrarModalNivel(): void {
    this.modalNivelVisible = false;
    this.nivelSeleccionado = null;
  }

  cerrarModalConfirmacion(): void {
    this.modalConfirmVisible = false;
    this.nivelSeleccionado = null;
  }
}
