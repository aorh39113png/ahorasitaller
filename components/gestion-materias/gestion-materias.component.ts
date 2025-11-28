import { Component, OnInit } from '@angular/core';
import { MateriasService } from '../../servicios/materias.service';
import { NivelesService } from '../../servicios/niveles.service';
import { ParalelosService } from '../../servicios/paralelos.service';
import { GeneralService } from '../../servicios/general.service';
import { AsignacionService } from '../../servicios/asignacion.service';
import { NotificationService } from '../../servicios/notification.service';
import { Materia, Nivel, Paralelo, Mapa, PageResponse } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
    selector: 'app-gestion-materias',
    standalone: false, // Asumo que no es standalone si no lo especificaste
    templateUrl: './gestion-materias.component.html',
    styleUrls: ['./gestion-materias.component.css']
})
export class GestionMateriasComponent implements OnInit {

    materias: Materia[] = [];
    listaNivelesActivos: Nivel[] = [];

    // --- Estado B-12.1 (Panel Paralelos) ---
    materiaSeleccionadaParaParalelos: Materia | null = null;
    paralelosAsignados: Mapa[] = [];
    listaParalelosActivos: Paralelo[] = [];
    paraleloParaAnadir: number = 0;
    gestionActual: number = new Date().getFullYear();
    mapaParaEliminar: Mapa | null = null;

    filtro = '';
    filtroEstado = 'TODOS';
    paginaActual = 1;
    itemsPorPagina = 10;
    totalPaginas = 0;
    modalMateriaVisible = false;
    modalConfirmVisible = false;
    modoEdicion = false;
    mensajeConfirmacion = '';
    materiaSeleccionada: Materia | null = null;
    tipoConfirmacion: 'eliminarMateria' | 'habilitarMateria' | 'eliminarParalelo' = 'eliminarMateria';

    constructor(
        private materiasService: MateriasService,
        private nivelesService: NivelesService,
        private paralelosService: ParalelosService,
        private generalService: GeneralService,
        private asignacionService: AsignacionService,
        private notificationService: NotificationService
    ) { }

    ngOnInit(): void {
        this.cargarMaterias();
        this.cargarNivelesParaDropdown();
        this.cargarDatosDelPanelParalelos();
    }


    cargarDatosDelPanelParalelos(): void {
        // 1. Cargar gestión actual
        this.generalService.getGestionActual().subscribe({
            next: (data) => {
                this.gestionActual = data.gestion;
            },
            error: (err) => console.error('Error al cargar gestión:', err)
        });

        // 2. Cargar lista de paralelos activos para el dropdown
        this.paralelosService.listarActivos().subscribe({
            next: (data) => {
                this.listaParalelosActivos = data;
            },
            error: (err) => console.error('Error al cargar paralelos activos:', err)
        });
    }


    cargarNivelesParaDropdown(): void {
        this.nivelesService.listarActivos().subscribe({
            next: (niveles) => {
                this.listaNivelesActivos = niveles;
            },
            error: (err) => console.error('Error al cargar niveles activos:', err)
        });
    }


    cargarMaterias(): void {
        this.materiasService.listarPaginado(
            this.filtro,
            this.filtroEstado,
            this.paginaActual,
            this.itemsPorPagina
        ).subscribe({
            next: (response: PageResponse<Materia>) => {

                // --- DEPURACIÓN TABLA PRINCIPAL ---
                console.log('Respuesta completa de materias:', response);
                if (response && response.content) {
                    response.content.forEach((materia, index) => {
                        if (!materia.nivel) {
                            console.error(`¡ERROR EN TABLA PRINCIPAL! Materia "${materia.nombre}" tiene un 'nivel' nulo.`);
                        }
                    });
                }
                // --- FIN DEPURACIÓN ---

                this.materias = response.content;
                this.totalPaginas = response.totalPages;
                this.paginaActual = response.number + 1;
            },
            error: (err) => console.error('Error al cargar materias:', err)
        });
    }

    // --- Disparadores de recarga ---

    onFiltroChange(): void {
        this.paginaActual = 1;
        this.cargarMaterias();
    }

    onEstadoChange(): void {
        this.paginaActual = 1;
        this.cargarMaterias();
    }

    cambiarPagina(nuevaPagina: number): void {
        if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
            this.paginaActual = nuevaPagina;
            this.cargarMaterias();
        }
    }

    // --- CRUD (Modales) ---

    abrirModalNuevo(): void {
        this.modoEdicion = false;
        this.materiaSeleccionada = {
            codmat: '',
            nombre: '',
            nivel: { codn: 0 }
        };
        this.modalMateriaVisible = true;
    }

    guardarMateria(materia: Materia): void {
        if (materia.nivel.codn === 0) {
            alert('Por favor, seleccione un nivel.');
            return;
        }
        this.materiasService.crear(materia).subscribe({
            next: () => {
                this.notificationService.showSuccess('Materia creada correctamente');
                this.cargarMaterias();
                this.cerrarModalMateria();
            },
            error: (err) => {
                console.error('Error al crear materia:', err);
                this.notificationService.showError('Error al crear materia');
            }
        });
    }

    abrirModalEditar(materia: Materia): void {
        this.modoEdicion = true;
        this.materiaSeleccionada = materia;
        this.modalMateriaVisible = true;
    }

    modificarMateria(materia: Materia): void {
        if (materia.nivel.codn === 0) {
            alert('Por favor, seleccione un nivel.');
            return;
        }
        this.materiasService.modificar(materia.codmat, materia).subscribe({
            next: () => {
                this.notificationService.showSuccess('Materia modificada correctamente');
                this.cargarMaterias();
                this.cerrarModalMateria();
            },
            error: (err) => {
                console.error('Error al modificar materia:', err);
                this.notificationService.showError('Error al modificar materia');
            }
        });
    }

    confirmarEliminar(materia: Materia): void {
        this.tipoConfirmacion = 'eliminarMateria';
        this.materiaSeleccionada = materia;
        this.mensajeConfirmacion = `¿Seguro de Eliminar la materia: ${materia.nombre}?`;
        this.modalConfirmVisible = true;
    }

    confirmarHabilitar(materia: Materia): void {
        this.tipoConfirmacion = 'habilitarMateria';
        this.materiaSeleccionada = materia;
        this.mensajeConfirmacion = `¿Seguro de Habilitar la materia: ${materia.nombre}?`;
        this.modalConfirmVisible = true;
    }

    ejecutarConfirmacion(): void {
        if (this.tipoConfirmacion === 'eliminarMateria' || this.tipoConfirmacion === 'habilitarMateria') {
            this.ejecutarConfirmacionMateria();
        } else if (this.tipoConfirmacion === 'eliminarParalelo') {
            this.ejecutarConfirmacionParalelo();
        }
    }

    ejecutarConfirmacionMateria(): void {
        if (!this.materiaSeleccionada?.codmat) return;
        const codmat = this.materiaSeleccionada.codmat;
        const obs =
            this.tipoConfirmacion === 'eliminarMateria'
                ? this.materiasService.eliminar(codmat)
                : this.materiasService.habilitar(codmat);

        obs.subscribe({
            next: () => {
                const accion = this.tipoConfirmacion === 'eliminarMateria' ? 'eliminada' : 'habilitada';
                this.notificationService.showSuccess(`Materia ${accion} correctamente`);
                this.cargarMaterias();
                this.cerrarModalConfirmacion();
            },
            error: (err) => {
                console.error('Error al actualizar estado de la materia:', err);
                this.notificationService.showError('Error al actualizar estado de la materia');
            },
        });
    }

    cerrarModalMateria(): void {
        this.modalMateriaVisible = false;
        this.materiaSeleccionada = null;
    }

    cerrarModalConfirmacion(): void {
        this.modalConfirmVisible = false;
        this.materiaSeleccionada = null;
        this.mapaParaEliminar = null;
    }

    // =============================================
    // LÓGICA: GESTIÓN PANEL PARALELOS (B-12.1)
    // =============================================

    abrirPanelParalelos(materia: Materia): void {
        if (this.materiaSeleccionadaParaParalelos?.codmat === materia.codmat) {
            this.cerrarPanelParalelos();
        } else {
            this.materiaSeleccionadaParaParalelos = materia;
            this.paraleloParaAnadir = 0;
            this.cargarParalelosAsignados();
        }
    }

    cerrarPanelParalelos(): void {
        this.materiaSeleccionadaParaParalelos = null;
        this.paralelosAsignados = [];
    }

    cargarParalelosAsignados(): void {
        if (!this.materiaSeleccionadaParaParalelos) return;
        const codmat = this.materiaSeleccionadaParaParalelos.codmat;

        this.asignacionService.getParalelosDeMateria(codmat, this.gestionActual).subscribe({
            next: (data) => {

                // --- ✅ INICIO DE LOGS DE DEPURACIÓN (B-1Z.1) ---
                console.log(`Paralelos asignados recibidos para ${codmat}:`, data);
                if (data && data.length > 0) {
                    data.forEach((mapa, index) => {
                        console.log(`Revisando mapa[${index}]:`, mapa);
                        // 1. Comprobamos si 'paralelo' existe
                        if (!mapa.paralelo) {
                            // 2. ¡ESTE ES EL ERROR QUE ESTÁS VIENDO!
                            console.error(`¡ERROR EN PANEL PARALELO! La asignación (ID: ${mapa.id.codpar}) tiene un 'paralelo' nulo o undefined.`);
                        } else {
                            console.log(`mapa[${index}] tiene paralelo: ${mapa.paralelo.nombre}`);
                        }
                    });
                }
                // --- ✅ FIN DE LOGS ---

                this.paralelosAsignados = data;
            },
            error: (err) => {
                console.error('Error al cargar paralelos asignados:', err);
                this.paralelosAsignados = [];
            }
        });
    }

    anadirParalelo(): void {
        if (this.paraleloParaAnadir === 0 || !this.materiaSeleccionadaParaParalelos) {
            alert('Seleccione un paralelo válido.');
            return;
        }
        const codmat = this.materiaSeleccionadaParaParalelos.codmat;
        const codpar = this.paraleloParaAnadir;

        const yaAsignado = this.paralelosAsignados.some(mapa => mapa.paralelo.codpar === codpar);
        if (yaAsignado) {
            alert('Este paralelo ya está asignado a la materia en esta gestión.');
            this.paraleloParaAnadir = 0;
            return;
        }

        this.asignacionService.asignarMateriaParalelo(codmat, codpar, this.gestionActual).subscribe({
            next: () => {
                this.notificationService.showSuccess('Paralelo asignado correctamente');
                this.paraleloParaAnadir = 0;
                this.cargarParalelosAsignados();
            },
            error: (err) => {
                console.error('Error al añadir paralelo:', err);
                this.notificationService.showError('Error al añadir paralelo');
            }
        });
    }

    confirmarEliminarParalelo(mapa: Mapa): void {
        this.tipoConfirmacion = 'eliminarParalelo';
        this.mapaParaEliminar = mapa;
        this.mensajeConfirmacion = `¿Seguro de Eliminar el paralelo "${mapa.paralelo.nombre}" de la materia?`;
        this.modalConfirmVisible = true;
    }

    ejecutarConfirmacionParalelo(): void {
        if (!this.mapaParaEliminar) return;
        const { codmat, codpar, gestion } = this.mapaParaEliminar.id;

        this.asignacionService.desasignarMateriaParalelo(codmat, codpar, gestion).subscribe({
            next: () => {
                this.notificationService.showSuccess('Paralelo desasignado correctamente');
                this.cargarParalelosAsignados();
                this.cerrarModalConfirmacion();
            },
            error: (err) => {
                console.error('Error al eliminar asignación:', err);
                this.notificationService.showError('Error al eliminar asignación');
                this.cerrarModalConfirmacion();
            }
        });
    }
}

