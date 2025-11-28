import { Component, OnInit } from '@angular/core';
import { AsignacionService } from '../../servicios/asignacion.service';
import { NotificationService } from '../../servicios/notification.service';
import { Menu, PageResponse, Role } from '../../models/auth-response.model';

@Component({
    selector: 'app-gestion-asignacion-roles',
    standalone: false,
    templateUrl: './gestion-asignacion-roles.component.html',
    styleUrl: './gestion-asignacion-roles.component.css'
})
export class GestionAsignacionRolesComponent implements OnInit {
    // --- Estado Panel Izquierdo (Roles) ---
    roles: Role[] = [];
    rolFiltro = '';
    paginaActualRol = 1;
    itemsPorPaginaRol = 10;
    totalPaginasRol = 0;
    rolSeleccionadoCodr: number | null = null; // ID del rol seleccionado

    // --- Estado Panel Derecho (Menús) ---
    menus: Menu[] = [];
    menuFiltro = '';
    filtroAsignado = 'TODOS'; // "TODOS", "SI", "NO"
    paginaActualMenu = 1;
    itemsPorPaginaMenu = 10;
    totalPaginasMenu = 0;

    constructor(
        private asignacionService: AsignacionService,
        private notificationService: NotificationService
    ) { }

    ngOnInit(): void {
        this.cargarRoles();
    }

    // ===================================
    // LÓGICA DE ROLES (Panel Izquierdo)
    // ===================================

    cargarRoles(): void {
        this.asignacionService
            .listarRolesPaginado(
                this.rolFiltro,
                this.paginaActualRol,
                this.itemsPorPaginaRol
            )
            .subscribe({
                next: (response: PageResponse<Role>) => {
                    this.roles = response.content;
                    this.totalPaginasRol = response.totalPages;
                    this.paginaActualRol = response.number + 1;
                },
                error: (err) => console.error('Error al cargar roles:', err),
            });
    }

    cambiarPaginaRol(nuevaPagina: number): void {
        if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginasRol) {
            this.paginaActualRol = nuevaPagina;
            this.cargarRoles();
        }
    }

    /**
     * Se dispara al seleccionar un Radio Button de Rol
     */
    onRolSelected(): void {
        // Resetea el panel derecho y carga los menús
        this.paginaActualMenu = 1;
        this.menuFiltro = '';
        this.filtroAsignado = 'TODOS';
        this.cargarMenus();
    }

    // =====================================
    // LÓGICA DE MENÚS (Panel Derecho)
    // =====================================

    cargarMenus(): void {
        if (!this.rolSeleccionadoCodr) {
            this.menus = []; // Limpia la lista si no hay rol
            return;
        }

        this.asignacionService
            .getMenusParaRol(
                this.rolSeleccionadoCodr,
                this.menuFiltro,
                this.filtroAsignado,
                this.paginaActualMenu,
                this.itemsPorPaginaMenu
            )
            .subscribe({
                next: (response: PageResponse<Menu>) => {
                    this.menus = response.content;
                    this.totalPaginasMenu = response.totalPages;
                    this.paginaActualMenu = response.number + 1;
                },
                error: (err) => console.error('Error al cargar menús:', err),
            });
    }

    cambiarPaginaMenu(nuevaPagina: number): void {
        if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginasMenu) {
            this.paginaActualMenu = nuevaPagina;
            this.cargarMenus();
        }
    }

    // Se dispara al cambiar el filtro "SI/NO/TODOS"
    onFiltroAsignadoChange(): void {
        this.paginaActualMenu = 1;
        this.cargarMenus();
    }

    /**
     * Se dispara al hacer Check/Uncheck en un Menú
     */
    onMenuToggle(menu: Menu, event: Event): void {
        const isChecked = (event.target as HTMLInputElement).checked;
        const codr = this.rolSeleccionadoCodr;
        const codm = menu.codm;

        if (!codr || !codm) {
            console.error('No se ha seleccionado un rol o el menú es inválido.');
            return;
        }

        const obs = isChecked
            ? this.asignacionService.asignarRolMenu(codr, codm)
            : this.asignacionService.desasignarRolMenu(codr, codm);

        obs.subscribe({
            next: () => {
                // Actualiza el estado local del objeto
                menu.asignado = isChecked;
                const accion = isChecked ? 'asignado' : 'desasignado';
                this.notificationService.showSuccess(`Menú ${accion} correctamente`);

                // Si el usuario está filtrando por "SI" o "NO",
                // recargamos la lista para que el item desaparezca.
                if (this.filtroAsignado !== 'TODOS') {
                    this.cargarMenus();
                }
            },
            error: (err) => {
                console.error('Error al asignar/desasignar:', err);
                this.notificationService.showError('Error al cambiar asignación');
                // Revierte el checkbox si la API falla
                (event.target as HTMLInputElement).checked = !isChecked;
            },
        });
    }
}
