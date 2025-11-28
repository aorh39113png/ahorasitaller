import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
// Importa las interfaces desde tu archivo central
import { Materia, PageResponse } from '../models/auth-response.model'; // Ajusta la ruta si es necesario

@Injectable({
  providedIn: 'root',
})
export class MateriasService {
  private apiURL = `${environment.apiURL}/api/materias`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-12. Listar, Filtrar y Paginar Materias
   */
  listarPaginado(
    filtro: string,
    estado: string,
    page: number,
    size: number
  ): Observable<PageResponse<Materia>> {

    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString()); // El doc (B-12) pide 10 [cite: 970]

    return this.http.get<PageResponse<Materia>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-12.3. Adicionar Nueva Materia
   */
  crear(materia: Materia): Observable<Materia> {
    // El backend espera un objeto Nivel, pero solo con el codn
    const body = {
      ...materia,
      nivel: { codn: materia.nivel.codn }
    };
    return this.http.post<Materia>(this.apiURL, body, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-12.4. Modificar Datos de la Materia
   */
  modificar(codmat: string, materia: Materia): Observable<Materia> {
    // El backend espera un objeto Nivel, pero solo con el codn
    const body = {
      ...materia,
      nivel: { codn: materia.nivel.codn }
    };
    return this.http.put<Materia>(`${this.apiURL}/${codmat}`, body, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-12.5. Eliminar (Baja lógica)
   */
  eliminar(codmat: string): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codmat}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * B-12.6. Habilitar Materia
   */
  habilitar(codmat: string): Observable<void> {
    return this.http.put<void>(
      `${this.apiURL}/${codmat}/habilitar`,
      {}, // Cuerpo vacío
      { headers: this.getAuthHeaders() }
    );
  }
}
