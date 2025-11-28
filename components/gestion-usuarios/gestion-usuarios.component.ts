import { Component, OnInit } from '@angular/core';
import { PersonalService, Personal } from '../../servicios/personal.service';
import { NotificationService } from '../../servicios/notification.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-gestion-usuarios',
  standalone: false,
  templateUrl: './gestion-usuarios.component.html',
  styleUrls: ['./gestion-usuarios.component.css'],
})
export class GestionUsuariosComponent implements OnInit {
  usuarios: Personal[] = [];
  //FILTROS 
  filtro = '';
  filtroEstado = 'TODOS';
  filtroTipo = '';

  // control de modales
  modalPersonaVisible = false;
  modalConfirmVisible = false;
  apiURL = environment.apiURL;

  // Contenido dinámico de la tabla de impresión
  printContent = '';

  modoEdicion = false;
  tipoConfirmacion: 'eliminar' | 'habilitar' = 'eliminar';
  mensajeConfirmacion = '';
  personaSeleccionada: Personal | null = null;

  nuevaPersona: Personal = this.resetPersona();
  nuevaFoto: File | null = null;

  // PAGINACIÓN

  paginaActual = 1;
  itemsPorPagina = 8;

  get totalPaginas(): number {
    return Math.ceil(this.filtrar().length / this.itemsPorPagina);
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
    }
  }

  get datosPaginados(): Personal[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.filtrar().slice(inicio, inicio + this.itemsPorPagina);
  }

   constructor(
    private personalService: PersonalService,
    private notificationService: NotificationService
  ) { }
  ngOnInit(): void {
    this.cargarUsuarios();
  }


  //  Listar personal

  cargarUsuarios(): void {
    this.personalService.listar().subscribe({
      next: (data) => {
        this.usuarios = data.map(p => ({ ...p, tieneClave: p['tieneClave'] ?? false }));
      },
      error: (err) => console.error(err)
    });
  }

  filtrar(): Personal[] {
    const f = this.filtro.toLowerCase();
    return this.usuarios.filter(
      (u) =>
        (`${u.nombre} ${u.ap} ${u.am ?? ''}`.toLowerCase().includes(f)) &&
        (this.filtroEstado === 'TODOS' ||
          (this.filtroEstado === 'ACTIVOS' && u.estado === 1) ||
          (this.filtroEstado === 'BAJAS' && u.estado === 0)) &&
        (this.filtroTipo === '' || u.tipo === this.filtroTipo)
    );
  }


  // nueva persona

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.nuevaPersona = this.resetPersona();
    this.nuevaFoto = null;
    this.modalPersonaVisible = true;
  }

 guardarPersona(): void {
    this.personalService.crear(this.nuevaPersona, this.nuevaFoto!).subscribe({
      next: () => {
        this.notificationService.showSuccess('Persona creada correctamente');
        this.cargarUsuarios();
        this.modalPersonaVisible = false;
      },
      error: (err) => {
        console.error('Error al guardar persona:', err);
        const mensaje = err?.error?.error || 'Error al crear persona';
        this.notificationService.showError(mensaje);
      },
    });
  }


  //modificar datos de la persona
  abrirModalEditar(p: Personal): void {
    this.modoEdicion = true;
    this.nuevaPersona = { ...p };
    this.nuevaFoto = null; // Resetear foto seleccionada
    this.modalPersonaVisible = true;
  }

  modificarPersona(): void {
    if (!this.nuevaPersona.codp) return;
    this.personalService
      .modificar(this.nuevaPersona.codp, this.nuevaPersona, this.nuevaFoto!)
      .subscribe({
        next: () => {
          this.notificationService.showSuccess('Persona modificada correctamente');
          this.cargarUsuarios();
          this.modalPersonaVisible = false;
        },
        error: (err) => {
          console.error('Error al modificar persona:', err);
          const mensaje = err?.error?.error || 'Error al modificar persona';
          this.notificationService.showError(mensaje);
        },
      });
  }

  quitarFoto(): void {
    if (this.nuevaPersona) {
      this.nuevaPersona.foto = 'DEFAULT'; // Flag para el backend
      this.nuevaFoto = null;
      this.notificationService.showSuccess('Foto removida. Se usará la imagen por defecto al guardar.');
    }
  }

  //elimina y habilita
  alternarEstado(u: Personal): void {
    if (!u.codp) return;
    if (u.estado === 1) this.confirmarEliminar(u);
    else this.confirmarHabilitar(u);
  }

  confirmarEliminar(p: Personal): void {
    this.tipoConfirmacion = 'eliminar';
    this.personaSeleccionada = p;
    this.mensajeConfirmacion = '¿Seguro de eliminar los datos de la persona?';
    this.modalConfirmVisible = true;
  }

  confirmarHabilitar(p: Personal): void {
    this.tipoConfirmacion = 'habilitar';
    this.personaSeleccionada = p;
    this.mensajeConfirmacion = '¿Seguro de habilitar a la persona?';
    this.modalConfirmVisible = true;
  }

  ejecutarConfirmacion(): void {
    if (!this.personaSeleccionada?.codp) return;

    const id = this.personaSeleccionada.codp;
    const obs =
      this.tipoConfirmacion === 'eliminar'
        ? this.personalService.eliminar(id)
        : this.personalService.habilitar(id);

    obs.subscribe({
      next: () => {
        const accion = this.tipoConfirmacion === 'eliminar' ? 'eliminada' : 'habilitada';
        this.notificationService.showSuccess(`Persona ${accion} correctamente`);
        this.cargarUsuarios();
        this.modalConfirmVisible = false;
      },
      error: (err) => {
        console.error('Error al actualizar estado:', err);
        this.notificationService.showError('Error al actualizar estado');
      },
    });
  }
imprimirUsuario(usuario: Personal): void {
    const datosContacto = [usuario.telefono, usuario.celular, usuario.telf]
      .filter(Boolean)
      .join(' / ');

    const nombreCompleto = [usuario.ap, usuario.am, usuario.nombre].filter(Boolean).join(' ');
    const estadoTexto = usuario.estado === 1 ? 'Activo' : 'Baja';

    const filasDetalle = [
      { etiqueta: 'CI', valor: usuario.cedula },
      { etiqueta: 'Fecha de nacimiento', valor: usuario.fnac },
      { etiqueta: 'Estado civil', valor: usuario.ecivil },
      { etiqueta: 'Género', valor: usuario.genero },
      { etiqueta: 'Dirección', valor: usuario.direc },
      { etiqueta: 'Teléfonos', valor: datosContacto },
      { etiqueta: 'Tipo', valor: usuario.tipo },
      { etiqueta: 'Estado', valor: estadoTexto },
      { etiqueta: 'Código', valor: usuario.codp?.toString() },
    ];

    const detalleHTML = filasDetalle
      .map((fila) => `<div class="print-row"><span class="print-label">${fila.etiqueta}:</span><span class="print-value">${fila.valor || '—'}</span></div>`)
      .join('');

    const contenido = `
      <div class="print-wrapper">
        <table class="print-table print-single-user">
          <tbody>
            <tr>
              <td class="print-col-photo">
                <img src="${this.obtenerFoto(usuario)}" alt="Foto de ${usuario.nombre ?? 'usuario'}" />
              </td>
              <td class="print-col-data">
                <div class="print-nombre">${nombreCompleto || 'Usuario sin nombre'}</div>
                ${detalleHTML}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    `;

    const estilos = `
      <style>
        @media print {
          @page { margin: 16mm; }
        }

        html, body {
          margin: 0;
          padding: 24px;
          background: #ffffff;
          color: #0b1120;
          font-family: "Times New Roman", Times, serif;
        }

        .print-wrapper {
          max-width: 780px;
          margin: 0 auto;
        }

        .print-table {
          width: 100%;
          border-collapse: collapse;
        }

        .print-table td {
          border: 1px solid #0f172a;
          padding: 10px;
          vertical-align: top;
          font-size: 14px;
          background: #ffffff;
        }

        .print-single-user {
          table-layout: fixed;
        }

        .print-col-photo {
          width: 180px;
          text-align: center;
        }

        .print-col-photo img {
          width: 140px;
          height: 170px;
          object-fit: cover;
          border: 1px solid #0f172a;
          border-radius: 6px;
          background: #ffffff;
        }

        .print-col-data {
          width: calc(100% - 180px);
        }

        .print-nombre {
          font-size: 18px;
          font-weight: 700;
          margin-bottom: 8px;
        }

        .print-row {
          display: flex;
          gap: 10px;
          margin-bottom: 6px;
          line-height: 1.4;
        }

        .print-label {
          min-width: 160px;
          font-weight: 700;
        }

        .print-value {
          flex: 1;
        }
      </style>
    `;

    const ventana = window.open('', '_blank', 'width=900,height=700');
    if (!ventana) return;

    ventana.document.open();
    ventana.document.write(`<!DOCTYPE html><html><head><title>Impresión de usuario</title>${estilos}</head><body>${contenido}</body></html>`);
    ventana.document.close();
    ventana.focus();
    ventana.print();
    ventana.close();
  }

  private obtenerFoto(persona: Personal): string {
    return persona.foto ? `${this.apiURL}/uploads/fotos/${persona.foto}` : 'assets/img/default.png';
  }


  cerrarModalPersona(): void {
    this.modalPersonaVisible = false;
  }

  cerrarModalConfirmacion(): void {
    this.modalConfirmVisible = false;
  }

  onFileSelected(file: File): void {
    if (file) {
      this.nuevaFoto = file;
      console.log("📸 Archivo recibido desde modal:", file.name);
    }
  }



  private resetPersona(): Personal {
    return {
      nombre: '',
      cedula: '',
      ap: '',
      am: '',
      genero: 'M',
      tipo: '',
      ecivil: 'S',
      estado: 1,
    };
  }
  // Modal de acceso
  modalAccesoVisible = false;
  modoAccesoEdicion = false;
  personaAcceso: Personal | null = null;
  nuevoAcceso = { login: '', password: '', repetir: '' };

  abrirModalAcceso(persona: Personal, edicion: boolean): void {
    this.personaAcceso = persona;
    this.modoAccesoEdicion = edicion;
    this.nuevoAcceso = { login: '', password: '', repetir: '' };
    this.modalAccesoVisible = true;
  }

  cerrarModalAcceso(): void {
    this.modalAccesoVisible = false;
  }

  // Guardar acceso (crear o modificar)
  guardarAcceso(): void {
    if (!this.personaAcceso?.codp) return;
    if (this.nuevoAcceso.password !== this.nuevoAcceso.repetir) {
      alert('Las contraseñas no coinciden.');
      return;
    }

    const usuario = {
      login: this.nuevoAcceso.login,
      password: this.nuevoAcceso.password,
      estado: 1,
      personal: { codp: this.personaAcceso.codp },
    };

    if (this.modoAccesoEdicion) {
      // editar acceso existente
      this.personalService.modificarUsuario(usuario.login, usuario).subscribe({
        next: () => {
          this.notificationService.showSuccess('Acceso modificado correctamente');
          this.cargarUsuarios();
          this.modalAccesoVisible = false;
        },
        error: (err) => {
          console.error('Error al modificar acceso:', err);
          this.notificationService.showError('Error al modificar acceso');
        },
      });
    } else {
      // crear nuevo acceso
      this.personalService.crearUsuario(usuario).subscribe({
        next: () => {
          this.notificationService.showSuccess('Acceso creado correctamente');
          this.cargarUsuarios();
          this.modalAccesoVisible = false;
        },
        error: (err) => {
          console.error('Error al crear acceso:', err);
          this.notificationService.showError('Error al crear acceso');
        },
      });
    }
  }

}
