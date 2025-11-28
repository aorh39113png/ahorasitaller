import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { PageResponse, Role } from '../models/auth-response.model';


@Injectable({
  providedIn: 'root',
})
export class RolesService {
  private apiURL = `${environment.apiURL}/api/roles`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-5. Listar, Filtrar y Paginar Roles
   *
   * @param filtro (para nombre)
   * @param estado ("TODOS", "ACTIVOS", "BAJAS")
   * @param page (Nro de página, 1-based para UI)
   * @param size (Tamaño de página, B-5 pide 15) [cite: 576]
   */
  listarPaginado(
    filtro: string,
    estado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Role>> {

    // Spring Pageable es 0-based, la UI es 1-based.
    const pageZeroBased = page - 1;

    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Role>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-5.1. Adicionar Nuevo Rol [cite: 583]
   */
  crear(rol: Role): Observable<Role> {
    return this.http.post<Role>(this.apiURL, rol, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-5.2. Modificar Datos del Rol [cite: 580]
   */
  modificar(codr: number, rol: Role): Observable<Role> {
    return this.http.put<Role>(`${this.apiURL}/${codr}`, rol, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-5.3. Eliminar (Baja lógica) [cite: 581]
   */
  eliminar(codr: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codr}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-5.4. Habilitar Rol [cite: 582]
   */
  habilitar(codr: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiURL}/${codr}/habilitar`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }
}
