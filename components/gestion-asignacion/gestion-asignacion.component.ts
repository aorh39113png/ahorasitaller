import { Component, OnInit } from '@angular/core';
import {
  AsignacionService,
} from '../../servicios/asignacion.service'; // Ajusta la ruta si es necesario
import { NotificationService } from '../../servicios/notification.service';
import { Menu, PageResponse, Proceso } from '../../models/auth-response.model';

@Component({
  selector: 'app-gestion-asignacion',
  standalone: false,
  templateUrl: './gestion-asignacion.component.html',
  styleUrl: './gestion-asignacion.component.css'
})
export class GestionAsignacionComponent implements OnInit {

  // --- Estado Panel Izquierdo (Menús) ---
  menus: Menu[] = [];
  menuFiltro = '';
  paginaActualMenu = 1;
  itemsPorPaginaMenu = 10;
  totalPaginasMenu = 0;
  menuSeleccionadoCodm: number | null = null; // ID del menú seleccionado

  // --- Estado Panel Derecho (Procesos) ---
  procesos: Proceso[] = [];
  procesoFiltro = '';
  filtroAsignado = 'TODOS'; // "TODOS", "SI", "NO"
  paginaActualProceso = 1;
  itemsPorPaginaProceso = 10;
  totalPaginasProceso = 0;

  constructor(
    private asignacionService: AsignacionService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.cargarMenus();
  }

  // ===================================
  // LÓGICA DE MENÚS (Panel Izquierdo)
  // ===================================

  cargarMenus(): void {
    this.asignacionService
      .listarMenusPaginado(
        this.menuFiltro,
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

  /**
   * Se dispara al seleccionar un Radio Button de Menú [cite: 775]
   */
  onMenuSelected(): void {
    // Resetea el panel derecho y carga los procesos
    this.paginaActualProceso = 1;
    this.procesoFiltro = '';
    this.filtroAsignado = 'TODOS';
    this.cargarProcesos();
  }

  // =====================================
  // LÓGICA DE PROCESOS (Panel Derecho)
  // =====================================

  cargarProcesos(): void {
    if (!this.menuSeleccionadoCodm) {
      this.procesos = []; // Limpia la lista si no hay menú
      return;
    }

    this.asignacionService
      .getProcesosParaMenu(
        this.menuSeleccionadoCodm,
        this.procesoFiltro,
        this.filtroAsignado,
        this.paginaActualProceso,
        this.itemsPorPaginaProceso
      )
      .subscribe({
        next: (response: PageResponse<Proceso>) => {
          this.procesos = response.content;
          this.totalPaginasProceso = response.totalPages;
          this.paginaActualProceso = response.number + 1;
        },
        error: (err) => console.error('Error al cargar procesos:', err),
      });
  }

  cambiarPaginaProceso(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginasProceso) {
      this.paginaActualProceso = nuevaPagina;
      this.cargarProcesos();
    }
  }

  // Se dispara al cambiar el filtro "SI/NO/TODOS"
  onFiltroAsignadoChange(): void {
    this.paginaActualProceso = 1;
    this.cargarProcesos();
  }

  /**
   * Se dispara al hacer Check/Uncheck en un Proceso [cite: 777, 779]
   */
  onProcesoToggle(proceso: Proceso, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const codm = this.menuSeleccionadoCodm;
    const codp = proceso.codp;

    if (!codm || !codp) {
      console.error('No se ha seleccionado un menú o el proceso es inválido.');
      return;
    }

    const obs = isChecked
      ? this.asignacionService.asignar(codm, codp)
      : this.asignacionService.desasignar(codm, codp);

    obs.subscribe({
      next: () => {
        // Actualiza el estado local del objeto
        proceso.asignado = isChecked;
        const accion = isChecked ? 'asignado' : 'desasignado';
        this.notificationService.showSuccess(`Proceso ${accion} correctamente`);

        // Si el usuario está filtrando por "SI" o "NO",
        // recargamos la lista para que el item desaparezca.
        if (this.filtroAsignado !== 'TODOS') {
          this.cargarProcesos();
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

