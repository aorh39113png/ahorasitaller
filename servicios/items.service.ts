import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Item, PageResponse } from '../models/auth-response.model';

@Injectable({
  providedIn: 'root',
})
export class ItemsService {
  private apiURL = `${environment.apiURL}/api/items`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  listarPaginado(filtro: string, estado: string, page: number, size: number): Observable<PageResponse<Item>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('estado', estado)
      .set('page', pageZeroBased.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Item>>(this.apiURL, { headers: this.getAuthHeaders(), params });
  }

  listarActivos(): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.apiURL}/activos`, { headers: this.getAuthHeaders() });
  }

  crear(item: Item): Observable<Item> {
    return this.http.post<Item>(this.apiURL, item, { headers: this.getAuthHeaders() });
  }

  modificar(codi: number, item: Item): Observable<Item> {
    return this.http.put<Item>(`${this.apiURL}/${codi}`, item, { headers: this.getAuthHeaders() });
  }

  eliminar(codi: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codi}`, { headers: this.getAuthHeaders() });
  }

  habilitar(codi: number): Observable<void> {
    return this.http.put<void>(`${this.apiURL}/${codi}/habilitar`, {}, { headers: this.getAuthHeaders() });
  }
}
