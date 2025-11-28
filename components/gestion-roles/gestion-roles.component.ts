import { Component, OnInit } from '@angular/core';
import { RolesService, } from '../../servicios/roles.service';
import { NotificationService } from '../../servicios/notification.service';
import { PageResponse, Role } from '../../models/auth-response.model';

@Component({
  selector: 'app-gestion-roles',
  templateUrl: './gestion-roles.component.html',
  styleUrls: ['./gestion-roles.component.css'],
  standalone: false
})
export class GestionRolesComponent implements OnInit {

  roles: Role[] = [];

  // FILTROS
  filtro = '';
  filtroEstado = 'TODOS'; // "TODOS", "ACTIVOS", "BAJAS"

  // PAGINACIÓN
  paginaActual = 1;
  itemsPorPagina = 15; // [cite: 576]
  totalPaginas = 0;

  // CONTROL DE MODALES
  modalRolVisible = false;
  modalConfirmVisible = false;
  modoEdicion = false;

  mensajeConfirmacion = '';
  rolSeleccionado: Role | null = null;
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

  constructor(
    private rolesService: RolesService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.cargarRoles();
  }

  /**
   * Carga la lista de roles desde el backend (paginada y filtrada)
   */
  cargarRoles(): void {
    this.rolesService.listarPaginado(
      this.filtro,
      this.filtroEstado,
      this.paginaActual,
      this.itemsPorPagina
    ).subscribe({
      next: (response: PageResponse<Role>) => {
        this.roles = response.content;
        this.totalPaginas = response.totalPages;
        this.paginaActual = response.number + 1; // Spring es 0-based
      },
      error: (err) => console.error('Error al cargar roles:', err)
    });
  }

  // --- Disparadores de recarga ---

  /**
   * Se llama al presionar Enter en el filtro [cite: 571]
   */
  onFiltroChange(): void {
    this.paginaActual = 1; // Resetea a la página 1
    this.cargarRoles();
  }

  /**
   * Se llama al cambiar el radio button de estado [cite: 572]
   */
  onEstadoChange(): void {
    this.paginaActual = 1; // Resetea a la página 1
    this.cargarRoles();
  }

  /**
   * Se llama desde el paginador [cite: 576]
   */
  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
      this.cargarRoles();
    }
  }

  // --- CRUD (Modales) ---

  // B-5.1. Adicionar Nuevo Rol
  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.rolSeleccionado = { nombre: '' }; // Objeto vacío para el formulario
    this.modalRolVisible = true;
  }

  guardarRol(rol: Role): void {
    this.rolesService.crear(rol).subscribe({
      next: () => {
        this.notificationService.showSuccess('Rol creado correctamente');
        this.cargarRoles();
        this.cerrarModalRol();
      },
      error: (err) => {
        console.error('Error al crear rol:', err);
        this.notificationService.showError('Error al crear rol');
      }
    });
  }

  // B-5.2. Modificar Rol
  abrirModalEditar(rol: Role): void {
    this.modoEdicion = true;
    this.rolSeleccionado = rol; // Pasa el rol existente al modal
    this.modalRolVisible = true;
  }

  modificarRol(rol: Role): void {
    if (!rol.codr) return;
    this.rolesService.modificar(rol.codr, rol).subscribe({
      next: () => {
        this.notificationService.showSuccess('Rol modificado correctamente');
        this.cargarRoles();
        this.cerrarModalRol();
      },
      error: (err) => {
        console.error('Error al modificar rol:', err);
        this.notificationService.showError('Error al modificar rol');
      }
    });
  }

  // B-5.3. Eliminar Rol
  confirmarEliminar(rol: Role): void {
    this.tipoConfirmacion = 'eliminar';
    this.rolSeleccionado = rol;
    this.mensajeConfirmacion = `¿Seguro de Eliminar datos del ROL: ${rol.nombre}?`; // [cite: 628]
    this.modalConfirmVisible = true;
  }

  // B-5.4. Habilitar Rol
  confirmarHabilitar(rol: Role): void {
    this.tipoConfirmacion = 'habilitar';
    this.rolSeleccionado = rol;
    this.mensajeConfirmacion = `¿Seguro de HABILITAR datos del ROL: ${rol.nombre}?`; // [cite: 641]
    this.modalConfirmVisible = true;
  }

  /**
   * Ejecuta la acción (Eliminar o Habilitar) confirmada en el modal
   */
  ejecutarConfirmacion(): void {
    if (!this.rolSeleccionado?.codr) return;

    const id = this.rolSeleccionado.codr;
    const obs =
      this.tipoConfirmacion === 'eliminar'
        ? this.rolesService.eliminar(id)
        : this.rolesService.habilitar(id);

    obs.subscribe({
      next: () => {
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminado' : 'habilitado';
        this.notificationService.showSuccess(`Rol ${accion} correctamente`);
        this.cargarRoles(); // Recarga la lista
        this.cerrarModalConfirmacion();
      },
      error: (err) => {
        console.error('Error al actualizar estado del rol:', err);
        this.notificationService.showError('Error al actualizar estado del rol');
      },
    });
  }

  // --- Control de Cierre de Modales ---

  cerrarModalRol(): void {
    this.modalRolVisible = false;
    this.rolSeleccionado = null;
  }

  cerrarModalConfirmacion(): void {
    this.modalConfirmVisible = false;
    this.rolSeleccionado = null;
  }
}
