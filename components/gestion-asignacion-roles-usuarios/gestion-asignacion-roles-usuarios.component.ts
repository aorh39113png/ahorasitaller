import { Component, OnInit } from '@angular/core';
import { PageResponse, Role, Usuario } from '../../models/auth-response.model';
import { AsignacionService } from '../../servicios/asignacion.service';
import { NotificationService } from '../../servicios/notification.service';

@Component({
  selector: 'app-gestion-asignacion-roles-usuarios',
  standalone: false,
  templateUrl: './gestion-asignacion-roles-usuarios.component.html',
  styleUrl: './gestion-asignacion-roles-usuarios.component.css'
})
export class GestionAsignacionRolesUsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuarioFiltro = '';
  paginaActualUsuario = 1;
  itemsPorPaginaUsuario = 10;
  totalPaginasUsuario = 0;
  usuarioSeleccionadoLogin: string | null = null;

  // --- Estado Panel Derecho (Roles) ---
  roles: Role[] = [];
  rolFiltro = '';
  filtroAsignado = 'TODOS';
  paginaActualRol = 1;
  itemsPorPaginaRol = 10;
  totalPaginasRol = 0;

  constructor(
    private asignacionService: AsignacionService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    console.log('GestionAsignacionUsurolComponent inicializado'); // ¿Se ejecuta esto?
    this.cargarUsuarios();
  }
  // ======================================
  // LÓGICA DE USUARIOS (Panel Izquierdo)
  // ======================================
  cargarUsuarios(): void {
    console.log('Llamando a cargarUsuarios con filtro:', this.usuarioFiltro); // ¿Se ejecuta esto?
    this.asignacionService
      .listarUsuariosPaginado(this.usuarioFiltro, this.paginaActualUsuario, this.itemsPorPaginaUsuario
      )
      .subscribe({
        next: (response: PageResponse<Usuario>) => {
          console.log('Respuesta recibida del backend:', response); // ¿Llega la respuesta? ¿Qué contiene?
          if (response && response.content) {
            this.usuarios = response.content;
            console.log('Usuarios asignados al componente:', this.usuarios); // ¿Se asignan los datos?
            this.totalPaginasUsuario = response.totalPages;
            this.paginaActualUsuario = response.number + 1;
          } else {
            console.warn('La respuesta del backend no tiene la estructura esperada:', response);
            this.usuarios = []; // Limpia por si acaso
          }
        },
        error: (err) => console.error('Error explícito al cargar usuarios:', err), // ¿Hay algún error aquí?
      });
  }

  cambiarPaginaUsuario(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginasUsuario) {
      this.paginaActualUsuario = nuevaPagina;
      this.cargarUsuarios();
    }
  }

  onUsuarioSelected(): void {
    this.paginaActualRol = 1;
    this.rolFiltro = '';
    this.filtroAsignado = 'TODOS';
    this.cargarRoles();
  }

  // ===================================
  // LÓGICA DE ROLES (Panel Derecho)
  // ===================================
  cargarRoles(): void {
    if (!this.usuarioSeleccionadoLogin) {
      this.roles = [];
      this.totalPaginasRol = 0;
      return;
    }

    this.asignacionService
      .getRolesParaUsuario(
        this.usuarioSeleccionadoLogin, this.rolFiltro, this.filtroAsignado,
        this.paginaActualRol, this.itemsPorPaginaRol
      ).subscribe({
        next: (response: PageResponse<Role>) => {
          this.roles = response.content;
          this.totalPaginasRol = response.totalPages;
          this.paginaActualRol = response.number + 1;
        },
        error: (err) => console.error('Error al cargar roles para el usuario:', err),
      });
  }

  cambiarPaginaRol(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginasRol) {
      this.paginaActualRol = nuevaPagina;
      this.cargarRoles();
    }
  }

  onFiltroAsignadoChange(): void {
    this.paginaActualRol = 1;
    this.cargarRoles();
  }

  onRolToggle(rol: Role, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const login = this.usuarioSeleccionadoLogin;
    const codr = rol.codr;

    if (!login || !codr) return;

    const obs = isChecked
      ? this.asignacionService.asignarUsuarioRol(login, codr)
      : this.asignacionService.desasignarUsuarioRol(login, codr);

    obs.subscribe({
      next: () => {
        rol.asignado = isChecked;
        const accion = isChecked ? 'asignado' : 'desasignado';
        this.notificationService.showSuccess(`Rol ${accion} correctamente`);
        if (this.filtroAsignado !== 'TODOS') {
          this.cargarRoles();
        }
      },
      error: (err) => {
        console.error('Error al asignar/desasignar rol:', err);
        this.notificationService.showError('Error al cambiar asignación');
        (event.target as HTMLInputElement).checked = !isChecked;
      },
    });
  }
}
