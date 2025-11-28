import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Mapa, MapaActivo, Menu, PageResponse, Proceso, Role, Usuario } from '../models/auth-response.model';


@Injectable({
  providedIn: 'root',
})
export class AsignacionService {
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);
private apiURL = `${environment.apiURL}/api/asignaciones`;
  private getAuthHeaders(): HttpHeaders {
const token = this.authService.getToken();
    console.log('Token obtenido en AsignacionService:', token);    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  // --- MÉTODOS PARA "LISTA DE MENÚS" (Izquierda) ---

  /**
   * B-7. Lista los Menús (panel izquierdo)
   */
  listarMenusPaginado(

    filtro: string,
    page: number,
    size: number
  ): Observable<PageResponse<Menu>> {
    console.log('AsignacionService: llamando a listarUsuariosPaginado');
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', 'ACTIVOS') // B-7 solo debe mostrar Menús activos
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Menu>>(
      `${environment.apiURL}/api/menus`,
      {
        headers: this.getAuthHeaders(),
        params: params,
      }
    );
  }

  // --- MÉTODOS PARA "LISTA DE PROCESOS" (Derecha) ---

  /**
   * B-7. Lista los Procesos (panel derecho) según el menú seleccionado
   */
  getProcesosParaMenu(
    codm: number,
    filtro: string,
    asignado: string, // "TODOS", "SI", "NO"
    page: number,
    size: number
  ): Observable<PageResponse<Proceso>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('asignado', asignado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Proceso>>(
      `${environment.apiURL}/api/procesos/para-menu/${codm}`,
      {
        headers: this.getAuthHeaders(),
        params: params,
      }
    );
  }

  // --- MÉTODOS PARA ASIGNAR (Check/Uncheck) ---

  /**
   * B-7. Asigna un proceso (POST a /api/asignaciones/mepro)
   */
  asignar(codm: number, codp: number): Observable<void> {
    let params = new HttpParams().set('codm', codm).set('codp', codp);
    return this.http.post<void>(
      `${environment.apiURL}/api/asignaciones/mepro`,
      null,
      { headers: this.getAuthHeaders(), params: params }
    );
  }

  /**
   * B-7. Desasigna un proceso (DELETE a /api/asignaciones/mepro)
   */
  desasignar(codm: number, codp: number): Observable<void> {
    let params = new HttpParams().set('codm', codm).set('codp', codp);
    return this.http.delete<void>(
      `${environment.apiURL}/api/asignaciones/mepro`,
      { headers: this.getAuthHeaders(), params: params }
    );
  }
  listarRolesPaginado(
    filtro: string,
    page: number,
    size: number
  ): Observable<PageResponse<Role>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', 'ACTIVOS') // B-8 solo debe mostrar Roles activos
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Role>>(
      `${environment.apiURL}/api/roles`, // Llama al RolesController
      {
        headers: this.getAuthHeaders(),
        params: params,
      }
    );
  }

  /**
   * B-8. Lista los Menús (panel derecho) según el ROL seleccionado
   */
  getMenusParaRol(
    codr: number,
    filtro: string,
    asignado: string, // "TODOS", "SI", "NO"
    page: number,
    size: number
  ): Observable<PageResponse<Menu>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('asignado', asignado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Menu>>(
      `${environment.apiURL}/api/menus/para-rol/${codr}`, // Llama al MenusController
      {
        headers: this.getAuthHeaders(),
        params: params,
      }
    );
  }

  /**
   * B-8. Asigna un menú a un rol (POST a /api/asignaciones/rolme)
   */
  asignarRolMenu(codr: number, codm: number): Observable<void> {
    let params = new HttpParams().set('codr', codr).set('codm', codm);
    return this.http.post<void>(
      `${environment.apiURL}/api/asignaciones/rolme`,
      null,
      { headers: this.getAuthHeaders(), params: params }
    );
  }

  /**
   * B-8. Desasigna un menú de un rol (DELETE a /api/asignaciones/rolme)
   */
  desasignarRolMenu(codr: number, codm: number): Observable<void> {
    let params = new HttpParams().set('codr', codr).set('codm', codm);
    return this.http.delete<void>(
      `${environment.apiURL}/api/asignaciones/rolme`,
      { headers: this.getAuthHeaders(), params: params }
    );
  }

  /**
   * B-9. Lista los Roles (panel derecho) según el USUARIO seleccionado
   */
  // --- MÉTODOS B-9 (USUROL) ---

  /**
   * B-9. Lista los Usuarios (panel izquierdo)
   */
  listarUsuariosPaginado(
    filtro: string,
    page: number,
    size: number
  ): Observable<PageResponse<Usuario>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Usuario>>(
      `${environment.apiURL}/api/usuarios`,
      {
        headers: this.getAuthHeaders(), // <-- El token se añade aquí
        params: params,
      }
    );
  }

  /**
   * B-9. Lista los Roles (panel derecho) según el USUARIO seleccionado
   */
  getRolesParaUsuario(
    login: string,
    filtro: string,
    asignado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Role>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('asignado', asignado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Role>>(
      `${environment.apiURL}/api/roles/para-usuario/${login}`,
      {
        headers: this.getAuthHeaders(), // <-- El token se añade aquí
        params: params,
      }
    );
  }

  /**
   * B-9. Asigna un rol a un usuario
   */
  asignarUsuarioRol(login: string, codr: number): Observable<void> {
    let params = new HttpParams().set('login', login).set('codr', codr);
    return this.http.post<void>(
      `${environment.apiURL}/api/asignaciones/usurol`,
      null,
      { headers: this.getAuthHeaders(), params: params } // <-- El token se añade aquí
    );
  }

  /**
   * B-9. Desasigna un rol de un usuario
   */
  desasignarUsuarioRol(login: string, codr: number): Observable<void> {
    let params = new HttpParams().set('login', login).set('codr', codr);
    return this.http.delete<void>(
      `${environment.apiURL}/api/asignaciones/usurol`,
      { headers: this.getAuthHeaders(), params: params } // <-- El token se añade aquí
    );
  }
  getParalelosDeMateria(codmat: string, gestion: number): Observable<Mapa[]> {
    let params = new HttpParams()
      .set('gestion', gestion.toString());

    return this.http.get<Mapa[]>(`${this.apiURL}/mapa/${codmat}`, {
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  /**
   * B-12.1. Asigna un paralelo a una materia (crea en MAPA)
   */
  asignarMateriaParalelo(codmat: string, codpar: number, gestion: number): Observable<Mapa> {
    let params = new HttpParams()
      .set('codmat', codmat)
      .set('codpar', codpar.toString())
      .set('gestion', gestion.toString());

    return this.http.post<Mapa>(`${this.apiURL}/mapa`, null, { // Cuerpo vacío
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  /**
   * B-12.1. Elimina la asignación de un paralelo (elimina de MAPA)
   */
  desasignarMateriaParalelo(codmat: string, codpar: number, gestion: number): Observable<void> {
    let params = new HttpParams()
      .set('codmat', codmat)
      .set('codpar', codpar.toString())
      .set('gestion', gestion.toString());

    return this.http.delete<void>(`${this.apiURL}/mapa`, {
      headers: this.getAuthHeaders(),
      params: params
    });
  }
  getMapasActivos(gestion: number): Observable<MapaActivo[]> {
    let params = new HttpParams().set('gestion', gestion.toString());
    return this.http.get<MapaActivo[]>(`${this.apiURL}/mapa-activos`, {
      headers: this.getAuthHeaders(),
      params: params
    });
  }
}
