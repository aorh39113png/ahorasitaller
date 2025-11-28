import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
// Importa las interfaces desde tu archivo central
import { Paralelo, PageResponse } from '../models/auth-response.model'; // Ajusta la ruta si es necesario

@Injectable({
  providedIn: 'root',
})
export class ParalelosService {
  private apiURL = `${environment.apiURL}/api/paralelos`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
listarActivos(): Observable<Paralelo[]> {
    return this.http.get<Paralelo[]>(`${this.apiURL}/activos`, {
      headers: this.getAuthHeaders(),
    });
  }
  /**
   * B-10. Listar, Filtrar y Paginar Paralelos
   */
  listarPaginado(
    filtro: string,
    estado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Paralelo>> {

    const pageZeroBased = page - 1; // Spring es 0-based
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString()); // El doc (B-10) pide 10 [cite: 866]

    return this.http.get<PageResponse<Paralelo>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-10.1. Adicionar Nuevo Paralelo
   */
  crear(paralelo: Paralelo): Observable<Paralelo> {
    return this.http.post<Paralelo>(this.apiURL, paralelo, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-10.2. Modificar Datos del Paralelo
   */
  modificar(codpar: number, paralelo: Paralelo): Observable<Paralelo> {
    return this.http.put<Paralelo>(`${this.apiURL}/${codpar}`, paralelo, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-10.3. Eliminar (Baja lógica)
   */
  eliminar(codpar: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codpar}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-10.4. Habilitar Paralelo
   */
  habilitar(codpar: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiURL}/${codpar}/habilitar`,
      {}, // Cuerpo vacío
      { headers: this.getAuthHeaders() }
    );
  }
}
