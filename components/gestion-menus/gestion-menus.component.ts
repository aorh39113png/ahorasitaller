import { Component, OnInit } from '@angular/core';
import { MenusService, } from '../../servicios/menus.service'; // Ajusta la ruta si es necesario
import { NotificationService } from '../../servicios/notification.service';
import { Menu, PageResponse } from '../../models/auth-response.model';

@Component({
  selector: 'app-gestion-menus',
  templateUrl: './gestion-menus.component.html',
  styleUrls: ['./gestion-menus.component.css'],
  standalone: false
})
export class GestionMenusComponent implements OnInit {

  menus: Menu[] = [];

  // FILTROS
  filtro = '';
  filtroEstado = 'TODOS'; // "TODOS", "ACTIVOS", "BAJAS"

  // PAGINACIÓN
  paginaActual = 1;
  itemsPorPagina = 10; // B-6 pide 10 elementos por página
  totalPaginas = 0;

  // CONTROL DE MODALES
  modalMenuVisible = false;
  modalConfirmVisible = false;
  modoEdicion = false;

  mensajeConfirmacion = '';
  menuSeleccionado: Menu | null = null;
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

  constructor(
    private menusService: MenusService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.cargarMenus();
  }

  /**
   * Carga la lista de menús desde el backend (paginada y filtrada)
   */
  cargarMenus(): void {
    this.menusService.listarPaginado(
      this.filtro,
      this.filtroEstado,
      this.paginaActual,
      this.itemsPorPagina
    ).subscribe({
      next: (response: PageResponse<Menu>) => {
        this.menus = response.content;
        this.totalPaginas = response.totalPages;
        this.paginaActual = response.number + 1; // Spring es 0-based
      },
      error: (err) => console.error('Error al cargar menús:', err)
    });
  }

  // --- Disparadores de recarga ---

  onFiltroChange(): void {
    this.paginaActual = 1;
    this.cargarMenus();
  }

  onEstadoChange(): void {
    this.paginaActual = 1;
    this.cargarMenus();
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
      this.cargarMenus();
    }
  }

  // --- CRUD (Modales) ---

  // B-6.1. Adicionar Nuevo Menú
  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.menuSeleccionado = { nombre: '' };
    this.modalMenuVisible = true;
  }

  guardarMenu(menu: Menu): void {
    this.menusService.crear(menu).subscribe({
      next: () => {
        this.notificationService.showSuccess('Menú creado correctamente');
        this.cargarMenus();
        this.cerrarModalMenu();
      },
      error: (err) => {
        console.error('Error al crear menú:', err);
        this.notificationService.showError('Error al crear menú');
      }
    });
  }

  // B-6.2. Modificar Menú
  abrirModalEditar(menu: Menu): void {
    this.modoEdicion = true;
    this.menuSeleccionado = menu;
    this.modalMenuVisible = true;
  }

  modificarMenu(menu: Menu): void {
    if (!menu.codm) return;
    this.menusService.modificar(menu.codm, menu).subscribe({
      next: () => {
        this.notificationService.showSuccess('Menú modificado correctamente');
        this.cargarMenus();
        this.cerrarModalMenu();
      },
      error: (err) => {
        console.error('Error al modificar menú:', err);
        this.notificationService.showError('Error al modificar menú');
      }
    });
  }

  // B-6.3. Eliminar Menú [cite: 722]
  confirmarEliminar(menu: Menu): void {
    this.tipoConfirmacion = 'eliminar';
    this.menuSeleccionado = menu;
    this.mensajeConfirmacion = `¿Seguro de Eliminar datos del MENÚ?`;
    this.modalConfirmVisible = true;
  }

  // B-6.4. Habilitar Menú [cite: 733]
  confirmarHabilitar(menu: Menu): void {
    this.tipoConfirmacion = 'habilitar';
    this.menuSeleccionado = menu;
    this.mensajeConfirmacion = `¿Seguro de HABILITAR datos del MENÚ?`;
    this.modalConfirmVisible = true;
  }

  /**
   * Ejecuta la acción (Eliminar o Habilitar) confirmada en el modal
   */
  ejecutarConfirmacion(): void {
    if (!this.menuSeleccionado?.codm) return;

    const id = this.menuSeleccionado.codm;
    const obs =
      this.tipoConfirmacion === 'eliminar'
        ? this.menusService.eliminar(id)
        : this.menusService.habilitar(id);

    obs.subscribe({
      next: () => {
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminado' : 'habilitado';
        this.notificationService.showSuccess(`Menú ${accion} correctamente`);
        this.cargarMenus();
        this.cerrarModalConfirmacion();
      },
      error: (err) => {
        console.error('Error al actualizar estado del menú:', err);
        this.notificationService.showError('Error al actualizar estado del menú');
      },
    });
  }

  // --- Control de Cierre de Modales ---

  cerrarModalMenu(): void {
    this.modalMenuVisible = false;
    this.menuSeleccionado = null;
  }

  cerrarModalConfirmacion(): void {
    this.modalConfirmVisible = false;
    this.menuSeleccionado = null;
  }
}
