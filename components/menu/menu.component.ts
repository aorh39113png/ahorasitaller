import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthServiceService } from '../../servicios/auth.service.service'; // 👈 Ajusta la ruta si es necesario
import { AuthResponse } from '../../models/auth-response.model'; // 👈 Ajusta la ruta

@Component({
  selector: 'app-menu', // (Asegúrate que este selector sea correcto)
  templateUrl: './menu.component.html',
  standalone:false,
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  // --- Variables para el Modal y el Formulario ---
  modalOpen = false;
  usuario = '';
  password = '';
  fechaActual = new Date();

  // --- 👇 LÓGICA DE SESIÓN ---
  // Tu HTML depende de una variable 'login'.
  // La mantenemos y la sincronizamos con el servicio.
  login: AuthResponse | null = null;
  selectedRole: any = null; // Para el dropdown de roles

  // Hacemos el servicio 'public' para que el HTML pueda usarlo
  constructor(
    public authService: AuthServiceService, // 👈 Inyectado como 'public'
    private router: Router
  ) {}

  ngOnInit(): void {
    // Al iniciar el componente, carga la sesión desde localStorage
    this.actualizarEstadoLogin();
  }

  /**
   * Carga la sesión actual desde el AuthService.
   */
  actualizarEstadoLogin(): void {
    if (this.authService.isLoggedIn()) {
      this.login = this.authService.getCurrentSession<AuthResponse>('currentUser');
      // Selecciona el primer rol por defecto en el dropdown
      this.selectedRole = this.login?.roles[0] ?? null;
    } else {
      this.login = null;
      this.selectedRole = null;
    }
  }

  /**
   * Llamado por el formulario de login.
   */
  ingreso_sistema(): void {
    this.authService.getLogin(this.usuario, this.password).subscribe({
      next: (response) => {
        // 1. Guarda la sesión
        this.authService.setCurrentSession('currentUser', response);
        // 2. Actualiza el estado local
        this.actualizarEstadoLogin();
        // 3. Cierra el modal
        this.cancelar();
        // 4. Redirige (opcional, si el home es solo para invitados)
        // this.router.navigate(['/UserGes']); // O a una página de "dashboard"
      },
      error: (err) => {
        console.error('Error en el login:', err);
        alert('Usuario o contraseña incorrectos');
      }
    });
  }

  /**
   * Llama al servicio de logout.
   */
  logout(): void {
    this.authService.logout();
    this.actualizarEstadoLogin();
  }

  // --- Control del Modal ---
  openModal(): void {
    this.modalOpen = true;
  }

  cancelar(): void {
    this.modalOpen = false;
    this.usuario = '';
    this.password = '';
  }

  // --- Lógica del Dropdown de Roles (opcional) ---
  selectRole(rol: any): void {
    // Esta función se llama si el usuario cambia de rol en el dropdown.
    // Aquí podrías añadir lógica para refrescar permisos si fuera necesario.
    console.log("Rol cambiado a:", rol.nombre);
    this.selectedRole = rol;
  }
}
