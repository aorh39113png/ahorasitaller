import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Dicta, PageResponse } from '../models/auth-response.model'; // Ajusta la ruta

@Injectable({
  providedIn: 'root'
})
export class DictaService {
  private apiURL = `${environment.apiURL}/api/dicta`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-16. Listar, Filtrar y Paginar Asignaciones
   */
  listarPaginado(
    filtro: string,
    estado: string,
    codn: number, // 0 si es "Todos los niveles"
    page: number,
    size: number
  ): Observable<PageResponse<Dicta>> {

    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    if (codn > 0) {
      params = params.set('codn', codn.toString());
    }

    return this.http.get<PageResponse<Dicta>>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  /**
   * B-16.1. Adicionar Nueva Asignación
   */
  crear(codmat: string, codpar: number, codp: number, gestion: number, login: string): Observable<Dicta> {
    let params = new HttpParams()
      .set('codmat', codmat)
      .set('codpar', codpar.toString())
      .set('codp', codp.toString())
      .set('gestion', gestion.toString())
      .set('login', login);

    return this.http.post<Dicta>(this.apiURL, null, { // Cuerpo vacío
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  /**
   * B-16.3. Eliminar (Baja lógica)
   */
  eliminar(id: Dicta['id']): Observable<void> {
    let params = new HttpParams()
      .set('codmat', id.codmat)
      .set('codpar', id.codpar.toString())
      .set('codp', id.codp.toString())
      .set('gestion', id.gestion.toString());

    return this.http.delete<void>(this.apiURL, {
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  /**
   * B-16. Habilitar (no está en la UI, pero lo implementamos)
   */
  habilitar(id: Dicta['id']): Observable<void> {
    let params = new HttpParams()
      .set('codmat', id.codmat)
      .set('codpar', id.codpar.toString())
      .set('codp', id.codp.toString())
      .set('gestion', id.gestion.toString());

    return this.http.put<void>(`${this.apiURL}/habilitar`, null, { // Cuerpo vacío
      headers: this.getAuthHeaders(),
      params: params
    });
  }
 modificar(
    // Clave Vieja (4 argumentos)
    oldCodmat: string,
    oldCodpar: number,
    oldCodp: number,
    oldGestion: number,
    // Clave Nueva (3 argumentos)
    newCodmat: string,
    newCodpar: number,
    newCodp: number,
    // Login (1 argumento)
    login: string
  ): Observable<Dicta> {

    let params = new HttpParams()
      // Clave Vieja
      .set('oldCodmat', oldCodmat)
      .set('oldCodpar', oldCodpar.toString())
      .set('oldCodp', oldCodp.toString())
      .set('oldGestion', oldGestion.toString())
      // Clave Nueva
      .set('newCodmat', newCodmat)
      .set('newCodpar', newCodpar.toString())
      .set('newCodp', newCodp.toString())
      .set('login', login);

    return this.http.put<Dicta>(this.apiURL, null, { // Llama al endpoint @PutMapping
      headers: this.getAuthHeaders(),
      params: params
    });
  }
 }
