import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthResponse, Role } from '../models/auth-response.model'; // Ajusta la ruta
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router'; // 👈 Importar Router

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  apiURL = environment.apiURL;
  private _http = inject(HttpClient);
  private router = inject(Router); // 👈 Inyectar Router

  constructor() { }

  getLogin(xlogin: string, xpass: string): Observable<AuthResponse> {
    const body = { username: xlogin, password: xpass };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Accept: 'application/json',
      }),
    };
    return this._http.post<AuthResponse>(`${this.apiURL}/api/auth/login`, body, httpOptions);
  }

  setCurrentSession(sessionName: string, data: AuthResponse): void {
    localStorage.setItem(sessionName, JSON.stringify(data));
  }

  getCurrentSession<T = any>(sessionName: string): T | null {
    const data = localStorage.getItem(sessionName);
    return data ? (JSON.parse(data) as T) : null;
  }

  getToken(): string {
    const session = this.getCurrentSession<AuthResponse>('currentUser');
    return session?.token ?? '';
  }

  getUsername(): string {
    const token = this.getToken();
    if (!token) {
      console.error('No se encontró token al intentar obtener el username');
      return '';
    }

    try {
      // 1. Obtenemos la parte del "payload" (la segunda parte del token)
      const payloadBase64 = token.split('.')[1];
      if (!payloadBase64) {
        return '';
      }

      // 2. Decodificamos de Base64 a un string JSON
      let safeBase64 = payloadBase64.replace(/-/g, '+').replace(/_/g, '/');
      while (safeBase64.length % 4) {
        safeBase64 += '=';
      }
      const payloadJson = atob(safeBase64);

      // 3. Parseamos el JSON
      const payload = JSON.parse(payloadJson);

      // 4. Devolvemos el 'sub' (subject), que es el username/login
      return payload.sub || '';

    } catch (e) {
      console.error('Error al decodificar el token JWT:', e);
      return '';
    }
  }

  // --- 👇 INICIO DE LAS CORRECCIONES (Añadir estos métodos) ---

  /**
   * Verifica si el usuario está actualmente logueado.
   */
  isLoggedIn(): boolean {
    // Si hay un token (y no está vacío), asumimos que está logueado
    // El backend lo validará si está expirado
    return !!this.getToken();
  }

  /**
   * Cierra la sesión del usuario.
   */
  logout(): void {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/home']); // Redirige a la página principal
  }

  /**
   * Obtiene la lista de roles del usuario actual desde la sesión.
   */
  getRoles(): Role[] {
    const session = this.getCurrentSession<AuthResponse>('currentUser');
    return session?.roles || [];
  }

  /**
   * Verifica si el usuario actual tiene un rol específico.
   * @param roleName El nombre del rol a verificar (Ej: "Administrador")
   */
  hasRole(roleName: string): boolean {
    const roles = this.getRoles();
    // Busca si alguno de los roles del usuario coincide con el nombre
    return roles.some(rol => rol.nombre === roleName);
  }

  /**
   * Verifica si el usuario tiene CUALQUIERA de los roles en una lista.
   * @param requiredRoles Un array de nombres de rol (Ej: ['Administrador', 'Docente'])
   */
  hasAnyRole(requiredRoles: string[]): boolean {
    const userRoles = this.getRoles().map(r => r.nombre);
    // Devuelve true si el usuario tiene al menos uno de los roles requeridos
    return requiredRoles.some(role => userRoles.includes(role));
  }
  // --- 👆 FIN DE LAS CORRECCIONES ---
}
