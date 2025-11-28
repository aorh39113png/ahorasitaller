import { Component, OnInit } from '@angular/core';
import { DictaService } from '../../servicios/dicta.service';
import { NivelesService } from '../../servicios/niveles.service';
import { GeneralService } from '../../servicios/general.service';
import { AsignacionService } from '../../servicios/asignacion.service';
import { PersonalService } from '../../servicios/personal.service';
import { AuthServiceService } from '../../servicios/auth.service.service'; // Para obtener el login
import { NotificationService } from '../../servicios/notification.service';
import { Dicta, Nivel, MapaActivo, Profesor, PageResponse } from '../../models/auth-response.model'; // Ajusta la ruta

@Component({
  selector: 'app-gestion-asignacion-dicta',
  standalone: false,
  templateUrl: './gestion-asignacion-dicta.component.html',
  styleUrls: ['./gestion-asignacion-dicta.component.css']
})
export class GestionAsignacionDictaComponent implements OnInit {

  // --- Datos para la TABLA PRINCIPAL ---
  asignaciones: Dicta[] = [];

  // --- Datos para los FILTROS ---
  filtro = '';
  filtroEstado = 'ACTIVOS'; // Inicia en Activos
  filtroNivel = 0; // 0 = Todos los niveles
  listaNivelesActivos: Nivel[] = [];

  // --- Datos para el MODAL ---
  listaMapasActivos: MapaActivo[] = [];
  listaProfesoresActivos: Profesor[] = [];
  gestionActual: number = new Date().getFullYear();
  loginUsuarioActual: string = '';

  // --- Paginación ---
  paginaActual = 1;
  itemsPorPagina = 10;
  totalPaginas = 0;

  // --- Control de Modales ---
  modalVisible = false;
  modalConfirmVisible = false;
  modoEdicion = false;
  mensajeConfirmacion = '';
  asignacionSeleccionada: Dicta | null = null; // Guarda la asignación *vieja* al editar
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';

  constructor(
    private dictaService: DictaService,
    private nivelesService: NivelesService,
    private generalService: GeneralService,
    private asignacionService: AsignacionService,
    private personalService: PersonalService,
    private authService: AuthServiceService, // Para obtener el login
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    try {
      this.loginUsuarioActual = this.authService.getUsername(); // O método similar
    } catch (e) {
      console.error("Error al obtener username (¿token inválido?):", e);
    }

    this.cargarAsignaciones();
    this.cargarDatosParaFiltrosYModal();
  }

  /**
   * Carga los datos necesarios para los dropdowns (filtro y modal)
   */
  cargarDatosParaFiltrosYModal(): void {
    // 1. Cargar Niveles (para el filtro)
    this.nivelesService.listarActivos().subscribe({
      next: (data) => this.listaNivelesActivos = data,
      error: (err) => console.error('Error al cargar niveles:', err)
    });

    // 2. Cargar Gestión (para el modal)
    this.generalService.getGestionActual().subscribe({
      next: (data) => {
        this.gestionActual = data.gestion;

        // 3. Cargar Mapas (Nivel-Materia-Paralelo) usando la gestión
        this.asignacionService.getMapasActivos(this.gestionActual).subscribe({
          next: (data) => this.listaMapasActivos = data,
          error: (err) => console.error('Error al cargar mapas activos:', err)
        });
      },
      error: (err) => console.error('Error al cargar gestión:', err)
    });

    // 4. Cargar Profesores (para el modal)
    this.personalService.listarProfesoresActivos().subscribe({
      next: (data) => this.listaProfesoresActivos = data,
      error: (err) => console.error('Error al cargar profesores:', err)
    });
  }

  /**
   * Carga la lista principal de asignaciones (paginada)
   */
  cargarAsignaciones(): void {
    this.dictaService.listarPaginado(
      this.filtro,
      this.filtroEstado,
      this.filtroNivel,
      this.paginaActual,
      this.itemsPorPagina
    ).subscribe({
      next: (response: PageResponse<Dicta>) => {
        this.asignaciones = response.content;
        this.totalPaginas = response.totalPages;
        this.paginaActual = response.number + 1;
      },
      error: (err) => console.error('Error al cargar asignaciones:', err)
    });
  }

  // --- Disparadores de recarga ---
  onFiltroChange(): void {
    this.paginaActual = 1;
    this.cargarAsignaciones();
  }
  onEstadoChange(): void {
    this.paginaActual = 1;
    this.cargarAsignaciones();
  }
  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
      this.cargarAsignaciones();
    }
  }

  // --- CRUD (Modales) ---

  // B-16.1. Adicionar
  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.asignacionSeleccionada = null;
    this.modalVisible = true;
  }

  // B-16.2. Modificar (Abre el modal)
  abrirModalEditar(asignacion: Dicta): void {
    this.modoEdicion = true;
    this.asignacionSeleccionada = asignacion; // Guarda la asignación *vieja*
    this.modalVisible = true;
  }

  /**
   * ✅ CORRECCIÓN: Esta función ahora maneja 'Crear' y 'Modificar'
   * Se llama cuando el modal emite el evento (guardar)
   */
  guardarAsignacion(data: { codmat: string, codpar: number, codp: number }): void {

    if (this.modoEdicion && this.asignacionSeleccionada) {
      // --- Lógica de Modificar (B-16.2) ---
      const idViejo = this.asignacionSeleccionada.id;

      // Comprobamos si realmente cambió algo
      if (idViejo.codmat === data.codmat && idViejo.codpar === data.codpar && idViejo.codp === data.codp) {
        alert("No se detectaron cambios.");
        this.cerrarModal();
        return;
      }

      this.dictaService.modificar(
        // IDs Viejos (para buscar y borrar)
        idViejo.codmat, idViejo.codpar, idViejo.codp, idViejo.gestion,
        // IDs Nuevos (para crear)
        data.codmat, data.codpar, data.codp,
        this.loginUsuarioActual
      ).subscribe({
        next: () => {
          this.notificationService.showSuccess('Asignación modificada correctamente');
          this.cargarAsignaciones();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al modificar asignación:', err);
          this.notificationService.showError('Error al modificar asignación');
        }
      });

    } else {
      // --- Lógica de Crear (B-16.1) ---
      this.dictaService.crear(
        data.codmat,
        data.codpar,
        data.codp,
        this.gestionActual,
        this.loginUsuarioActual
      ).subscribe({
        next: () => {
          this.notificationService.showSuccess('Asignación creada correctamente');
          this.cargarAsignaciones();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al crear asignación:', err);
          this.notificationService.showError('Error al crear asignación');
        }
      });
    }
  }
  // --- FIN DE LA CORRECCIÓN ---

  // B-16.3. Eliminar
  confirmarEliminar(asignacion: Dicta): void {
    this.tipoConfirmacion = 'eliminar';
    this.asignacionSeleccionada = asignacion;
    this.mensajeConfirmacion = `¿Seguro de Eliminar esta Asignación?`;
    this.modalConfirmVisible = true;
  }

  // Habilitar
  confirmarHabilitar(asignacion: Dicta): void {
    this.tipoConfirmacion = 'habilitar';
    this.asignacionSeleccionada = asignacion;
    this.mensajeConfirmacion = `¿Seguro de Habilitar esta Asignación?`;
    this.modalConfirmVisible = true;
  }

  ejecutarConfirmacion(): void {
    if (!this.asignacionSeleccionada) return;

    const id = this.asignacionSeleccionada.id;
    const obs =
      this.tipoConfirmacion === 'eliminar'
        ? this.dictaService.eliminar(id)
        : this.dictaService.habilitar(id);

    obs.subscribe({
      next: () => {
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminada' : 'habilitada';
        this.notificationService.showSuccess(`Asignación ${accion} correctamente`);
        this.cargarAsignaciones();
        this.cerrarModalConfirmacion();
      },
      error: (err) => {
        console.error('Error al actualizar estado:', err);
        this.notificationService.showError('Error al actualizar estado');
      },
    });
  }

  // --- Control de Cierre de Modales ---
  cerrarModal(): void {
    this.modalVisible = false;
    this.asignacionSeleccionada = null;
  }
  cerrarModalConfirmacion(): void {
    this.modalConfirmVisible = false;
    this.asignacionSeleccionada = null;
  }
}
