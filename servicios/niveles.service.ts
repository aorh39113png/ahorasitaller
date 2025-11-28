import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
// Importa las interfaces desde tu archivo central
import { Nivel, PageResponse } from '../models/auth-response.model'; // Ajusta la ruta si es necesario

@Injectable({
  providedIn: 'root',
})
export class NivelesService {
  private apiURL = `${environment.apiURL}/api/niveles`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-13. Listar, Filtrar y Paginar Niveles
   */
  listarPaginado(
    filtro: string,
    estado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Nivel>> {

    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString()); // El doc (B-13) pide 10

    return this.http.get<PageResponse<Nivel>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-13.1. Adicionar Nuevo Nivel
   */
  crear(nivel: Nivel): Observable<Nivel> {
    return this.http.post<Nivel>(this.apiURL, nivel, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-13.2. Modificar Datos del Nivel
   */
  modificar(codn: number, nivel: Nivel): Observable<Nivel> {
    return this.http.put<Nivel>(`${this.apiURL}/${codn}`, nivel, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-13.3. Eliminar (Baja lógica)
   */
  eliminar(codn: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codn}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-13.4. Habilitar Nivel
   */
  habilitar(codn: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiURL}/${codn}/habilitar`,
      {}, // Cuerpo vacío
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Extra: Obtiene todos los niveles ACTIVOS (para el dropdown de Materias B-12)
   */
  listarActivos(): Observable<Nivel[]> {
    return this.http.get<Nivel[]>(`${this.apiURL}/activos`, {
      headers: this.getAuthHeaders(),
    });
  }
}
