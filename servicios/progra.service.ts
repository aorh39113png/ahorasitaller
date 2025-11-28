import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Progra, PageResponse } from '../models/auth-response.model';

@Injectable({ providedIn: 'root' })
export class PrograService {
  private apiURL = `${environment.apiURL}/api/progra`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  }

  listarPaginado(filtro: string, estado: string, codn: number, page: number, size: number): Observable<PageResponse<Progra>> {
    const pageZeroBased = page - 1;
    let params = new HttpParams()
      .set('filtro', filtro).set('estado', estado)
      .set('page', pageZeroBased.toString()).set('size', size.toString());
    if (codn > 0) params = params.set('codn', codn.toString());

    return this.http.get<PageResponse<Progra>>(this.apiURL, { headers: this.getAuthHeaders(), params });
  }

  crear(codmat: string, codpar: number, codp: number, gestion: number, login: string): Observable<Progra> {
    let params = new HttpParams()
      .set('codmat', codmat).set('codpar', codpar.toString())
      .set('codp', codp.toString()).set('gestion', gestion.toString()).set('login', login);
    return this.http.post<Progra>(this.apiURL, null, { headers: this.getAuthHeaders(), params });
  }

  modificar(idViejo: Progra['id'], codmatNew: string, codparNew: number, codpNew: number, login: string): Observable<Progra> {
    let params = new HttpParams()
      .set('oldCodmat', idViejo.codmat).set('oldCodpar', idViejo.codpar.toString())
      .set('oldCodp', idViejo.codp.toString()).set('oldGestion', idViejo.gestion.toString())
      .set('newCodmat', codmatNew).set('newCodpar', codparNew.toString())
      .set('newCodp', codpNew.toString()).set('login', login);
    return this.http.put<Progra>(this.apiURL, null, { headers: this.getAuthHeaders(), params });
  }

  eliminar(id: Progra['id']): Observable<void> {
    let params = new HttpParams()
      .set('codmat', id.codmat).set('codpar', id.codpar.toString())
      .set('codp', id.codp.toString()).set('gestion', id.gestion.toString());
    return this.http.delete<void>(this.apiURL, { headers: this.getAuthHeaders(), params });
  }

  habilitar(id: Progra['id']): Observable<void> {
    let params = new HttpParams()
      .set('codmat', id.codmat).set('codpar', id.codpar.toString())
      .set('codp', id.codp.toString()).set('gestion', id.gestion.toString());
    return this.http.put<void>(`${this.apiURL}/habilitar`, null, { headers: this.getAuthHeaders(), params });
  }
}
