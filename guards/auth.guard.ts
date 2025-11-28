import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core'; // 👈 Importa inject
import { AuthServiceService } from '../core/servicios/auth.service.service';

/**
 * Este es un Guardián funcional que decide si una ruta puede ser activada.
 */
export const authGuard: CanActivateFn = (route, state) => {

  // --- INICIO DE LA CORRECCIÓN ---

  // En los guardianes funcionales, usamos inject() para obtener los servicios
  const authService = inject(AuthServiceService);
  const router = inject(Router);

  // 1. ¿Está el usuario logueado?
  if (!authService.isLoggedIn()) {
    console.warn('Acceso denegado - Usuario no logueado');
    // No está logueado, redirigir a la página de login (o home)
    router.navigate(['/home']); //
    return false;
  }

  // 2. ¿La ruta requiere roles específicos?
  // (Obtenemos los roles desde el 'data' de la ruta en app-routing.module.ts)
  const requiredRoles = route.data['roles'] as string[];

  // Si la ruta no define 'roles', solo con estar logueado es suficiente
  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  // 3. ¿Tiene el usuario el rol requerido?
  if (authService.hasAnyRole(requiredRoles)) {
    return true; // Sí tiene permiso
  } else {
    console.warn('Acceso denegado - El usuario no tiene el rol requerido');
    // Tiene login, pero no el rol. Redirigir a 'home'.
    router.navigate(['/home']);
    return false;
  }

  // --- FIN DE LA CORRECCIÓN ---
};
