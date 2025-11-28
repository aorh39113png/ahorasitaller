import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Menu, PageResponse } from '../models/auth-response.model';


@Injectable({
  providedIn: 'root',
})
export class MenusService {
  private apiURL = `${environment.apiURL}/api/menus`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-6. Listar, Filtrar y Paginar Menús
   */
  listarPaginado(
    filtro: string,
    estado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Menu>> {

    const pageZeroBased = page - 1; // Spring es 0-based
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Menu>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-6.1. Adicionar Nuevo Menú
   */
  crear(menu: Menu): Observable<Menu> {
    return this.http.post<Menu>(this.apiURL, menu, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-6.2. Modificar Datos del Menú
   */
  modificar(codm: number, menu: Menu): Observable<Menu> {
    return this.http.put<Menu>(`${this.apiURL}/${codm}`, menu, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-6.3. Eliminar (Baja lógica)
   */
  eliminar(codm: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codm}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-6.4. Habilitar Menú
   */
  habilitar(codm: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiURL}/${codm}/habilitar`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }
}
