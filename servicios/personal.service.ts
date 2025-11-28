import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthServiceService } from './auth.service.service';
import { Estudiante, Profesor } from '../models/auth-response.model';

export interface Personal {
  codp?: number;
  nombre: string;
  cedula?: string;
  ap: string;
  am?: string;
  estado?: number;
  fnac?: string;
  ecivil?: string;
  genero: string;
  direc?: string;
  telf?: string;
  telefono?: string;
  celular?: string;
  tipo: string;
  foto?: string;
  tieneClave?: boolean | number;
}


@Injectable({
  providedIn: 'root',
})
export class PersonalService {
  private apiURL = `${environment.apiURL}/api/personal`;
  private http = inject(HttpClient);
  private authService = inject(AuthServiceService);

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  listar(): Observable<Personal[]> {
    return this.http.get<Personal[]>(this.apiURL, { headers: this.getAuthHeaders() });
  }

  crear(personal: Personal, foto?: File): Observable<Personal> {
  const formData = new FormData();
  formData.append('personal', new Blob([JSON.stringify(personal)], { type: 'application/json' }));
  if (foto) {
    console.log("📤 Enviando archivo:", foto.name);
    formData.append('foto', foto);
  } else {
    console.log("⚠️ No se adjuntó archivo");
  }

  return this.http.post<Personal>(this.apiURL, formData, {
    headers: this.getAuthHeaders(), // solo Authorization
  });
}


  modificar(codp: number, personal: Personal, foto?: File): Observable<Personal> {
    const formData = new FormData();
    formData.append('personal', new Blob([JSON.stringify(personal)], { type: 'application/json' }));
    if (foto) formData.append('foto', foto);

    return this.http.put<Personal>(`${this.apiURL}/${codp}`, formData, { headers: this.getAuthHeaders() });
  }

  eliminar(codp: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${codp}`, { headers: this.getAuthHeaders() });
  }

  habilitar(codp: number): Observable<void> {
    return this.http.put<void>(`${this.apiURL}/${codp}/habilitar`, {}, { headers: this.getAuthHeaders() });
  }
  registrarEnDatos(codp: number, cedula: string): Observable<any> {
  const params = new URLSearchParams({ codp: codp.toString(), cedula });
  return this.http.post(`${environment.apiURL}/api/datos?${params.toString()}`, {}, {
    headers: this.getAuthHeaders(),
  });
}
crearUsuario(usuario: any): Observable<any> {
  return this.http.post(`${environment.apiURL}/api/usuarios`, usuario, {
    headers: this.getAuthHeaders(),
  });
}

modificarUsuario(login: string, usuario: any): Observable<any> {
  return this.http.put(`${environment.apiURL}/api/usuarios/${login}`, usuario, {
    headers: this.getAuthHeaders(),
  });
}

 listarProfesoresActivos(): Observable<Profesor[]> {
    return this.http.get<Profesor[]>(`${this.apiURL}/profesores-activos`, {
      headers: this.getAuthHeaders()
    });
  }
listarEstudiantesActivos(): Observable<Estudiante[]> {
    // Llama al nuevo endpoint que creamos en PersonalController del backend
    // Asegúrate de que el endpoint exista en el backend (PersonalController.java)
    return this.http.get<Estudiante[]>(`${this.apiURL}/estudiantes-activos`, {
      headers: this.getAuthHeaders()
    });
  }
}
