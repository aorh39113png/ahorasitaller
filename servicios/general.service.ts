import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Gestion } from '../models/auth-response.model'; // Ajusta la ruta

@Injectable({
  providedIn: 'root'
})
export class GeneralService {
  private apiURL = `${environment.apiURL}/api/general`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * B-12.1. Obtiene la gestión actual
   */
  getGestionActual(): Observable<Gestion> {
    return this.http.get<Gestion>(`${this.apiURL}/gestion-actual`, {
      headers: this.getAuthHeaders(),
    });
  }
}
