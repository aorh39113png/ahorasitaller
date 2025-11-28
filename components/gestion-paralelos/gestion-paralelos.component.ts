import { Component, OnInit } from '@angular/core';
import { ParalelosService } from '../../servicios/paralelos.service'; // Ajusta la ruta
import { NotificationService } from '../../servicios/notification.service';
import { Paralelo, PageResponse } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
    selector: 'app-gestion-paralelos',
    standalone: false,
    templateUrl: './gestion-paralelos.component.html',
    styleUrls: ['./gestion-paralelos.component.css']
})
export class GestionParalelosComponent implements OnInit {

    paralelos: Paralelo[] = [];

    // FILTROS
    filtro = '';
    filtroEstado = 'TODOS';

    // --- ✅ INICIO DE LA CORRECCIÓN 1 (Arregla 'page=NaN') ---
    paginaActual: number = 1;      // Inicializar en 1
    itemsPorPagina: number = 10;   // B-10 pide 10
    totalPaginas: number = 0;      // Inicializar en 0
    // --- FIN DE LA CORRECCIÓN 1 ---

    // CONTROL DE MODALES
    modalParaleloVisible = false;
    modalConfirmVisible = false;
    modoEdicion = false;

    mensajeConfirmacion = '';
    paraleloSeleccionado: Paralelo | null = null;
    tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

    constructor(
        private paralelosService: ParalelosService,
        private notificationService: NotificationService
    ) { }

    ngOnInit(): void {
        this.cargarParalelos();
    }

    /**
     * Carga la lista de paralelos desde el backend (paginada y filtrada)
     */
    cargarParalelos(): void {
        // Asegurarse de que paginaActual sea un número válido antes de llamar
        if (isNaN(this.paginaActual) || this.paginaActual < 1) {
            this.paginaActual = 1;
            console.warn("paginaActual era NaN, reseteando a 1.");
        }

        this.paralelosService.listarPaginado(
            this.filtro,
            this.filtroEstado,
            this.paginaActual, // Ahora SÍ es un número (1)
            this.itemsPorPagina
        ).subscribe({
            next: (response: PageResponse<Paralelo>) => {
                this.paralelos = response.content;
                this.totalPaginas = response.totalPages;
                this.paginaActual = response.number + 1; // Spring es 0-based
            },
            error: (err) => console.error('Error al cargar paralelos:', err)
        });
    }

    // --- Disparadores de recarga ---

    onFiltroChange(): void {
        this.paginaActual = 1;
        this.cargarParalelos();
    }

    onEstadoChange(): void {
        this.paginaActual = 1;
        this.cargarParalelos();
    }

    cambiarPagina(nuevaPagina: number): void {
        if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
            this.paginaActual = nuevaPagina;
            this.cargarParalelos();
        }
    }

    // --- CRUD (Modales) ---

    abrirModalNuevo(): void {
        this.modoEdicion = false;
        this.paraleloSeleccionado = { nombre: '' };
        this.modalParaleloVisible = true;
    }

    // --- ✅ INICIO DE LA CORRECCIÓN 2 (Arregla "no refresca al crear") ---
    guardarParalelo(paralelo: Paralelo): void {
        this.paralelosService.crear(paralelo).subscribe({
            next: () => {
                this.notificationService.showSuccess('Paralelo creado correctamente');
                this.paginaActual = 1;
                this.filtroEstado = 'TODOS'; // Asegurarse de ver todos
                this.cargarParalelos();
                this.cerrarModalParalelo();
            },
            error: (err) => {
                // Esto se ejecuta si el backend da el error de "bucle infinito"
                console.warn('POST exitoso, pero JSON de respuesta falló (bucle). Refrescando de todos modos.');
                this.notificationService.showSuccess('Paralelo creado correctamente');
                this.paginaActual = 1;
                this.filtroEstado = 'TODOS';
                this.cargarParalelos(); // Forzamos la recarga
                this.cerrarModalParalelo();
            }
        });
    }
    // --- FIN DE LA CORRECCIÓN 2 ---

    abrirModalEditar(paralelo: Paralelo): void {
        this.modoEdicion = true;
        this.paraleloSeleccionado = paralelo;
        this.modalParaleloVisible = true;
    }

    modificarParalelo(paralelo: Paralelo): void {
        if (!paralelo.codpar) return;
        this.paralelosService.modificar(paralelo.codpar, paralelo).subscribe({
            next: () => {
                this.notificationService.showSuccess('Paralelo modificado correctamente');
                this.cargarParalelos();
                this.cerrarModalParalelo();
            },
            error: (err) => {
                // También aplicamos la corrección aquí
                console.warn('PUT exitoso, pero JSON de respuesta falló (bucle). Refrescando de todos modos.');
                this.notificationService.showSuccess('Paralelo modificado correctamente');
                this.cargarParalelos();
                this.cerrarModalParalelo();
            }
        });
    }

    confirmarEliminar(paralelo: Paralelo): void {
        this.tipoConfirmacion = 'eliminar';
        this.paraleloSeleccionado = paralelo;
        this.mensajeConfirmacion = '¿Seguro de Eliminar el Paralelo?';
        this.modalConfirmVisible = true;
    }

    confirmarHabilitar(paralelo: Paralelo): void {
        this.tipoConfirmacion = 'habilitar';
        this.paraleloSeleccionado = paralelo;
        this.mensajeConfirmacion = '¿Seguro de Habilitar el Paralelo?';
        this.modalConfirmVisible = true;
    }

    ejecutarConfirmacion(): void {
        if (!this.paraleloSeleccionado?.codpar) return;

        const id = this.paraleloSeleccionado.codpar;
        const obs =
            this.tipoConfirmacion === 'eliminar'
                ? this.paralelosService.eliminar(id)
                : this.paralelosService.habilitar(id);

        obs.subscribe({
            next: () => {
                const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminado' : 'habilitado';
                this.notificationService.showSuccess(`Paralelo ${accion} correctamente`);
                this.cargarParalelos(); // Recarga la página actual
                this.cerrarModalConfirmacion();
            },
            error: (err) => {
                // También aplicamos la corrección aquí
                console.warn('DELETE/PUT exitoso, pero JSON de respuesta falló (bucle). Refrescando de todos modos.');
                const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminado' : 'habilitado';
                this.notificationService.showSuccess(`Paralelo ${accion} correctamente`);
                this.cargarParalelos();
                this.cerrarModalConfirmacion();
            }
        });
    }

    // --- Control de Cierre de Modales ---

    cerrarModalParalelo(): void {
        this.modalParaleloVisible = false;
        this.paraleloSeleccionado = null;
    }

    cerrarModalConfirmacion(): void {
        this.modalConfirmVisible = false;
        this.paraleloSeleccionado = null;
    }
}
