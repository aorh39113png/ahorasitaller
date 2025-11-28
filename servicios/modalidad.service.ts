import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Modalidad, PageResponse } from '../models/auth-response.model';

@Injectable({
  providedIn: 'root',
})
export class ModalidadService {
  private apiURL = `${environment.apiURL}/api/modalidad`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  listarPaginado(filtro: string, estado: string, page: number, size: number): Observable<PageResponse<Modalidad>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Modalidad>>(this.apiURL, { headers: this.getAuthHeaders(), params });
  }

  listarActivos(): Observable<Modalidad[]> {
    return this.http.get<Modalidad[]>(`${this.apiURL}/activos`, { headers: this.getAuthHeaders() });
  }

  crear(modalidad: Modalidad): Observable<Modalidad> {
    return this.http.post<Modalidad>(this.apiURL, modalidad, { headers: this.getAuthHeaders() });
  }

  modificar(codm: number, modalidad: Modalidad): Observable<Modalidad> {
    return this.http.put<Modalidad>(`${this.apiURL}/${codm}`, modalidad, { headers: this.getAuthHeaders() });
  }

  eliminar(codm: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codm}`, { headers: this.getAuthHeaders() });
  }

  habilitar(codm: number): Observable<void> {
    return this.http.put<void>(`${this.apiURL}/${codm}/habilitar`, {}, { headers: this.getAuthHeaders() });
  }
}