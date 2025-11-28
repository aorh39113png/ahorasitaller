import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './core/components/home/home.component';
import { ContactosComponent } from './core/components/contactos/contactos.component';
import { GestionUsuariosComponent } from './core/components/gestion-usuarios/gestion-usuarios.component';
import { GestionRolesComponent } from './core/components/gestion-roles/gestion-roles.component';
import { GestionMenusComponent } from './core/components/gestion-menus/gestion-menus.component';
import { GestionAsignacionComponent } from './core/components/gestion-asignacion/gestion-asignacion.component';
import { GestionAsignacionRolesComponent } from './core/components/gestion-asignacion-roles/gestion-asignacion-roles.component';
import { GestionAsignacionRolesUsuariosComponent } from './core/components/gestion-asignacion-roles-usuarios/gestion-asignacion-roles-usuarios.component';
import { GestionParalelosComponent } from './core/components/gestion-paralelos/gestion-paralelos.component';
import { GestionNivelesComponent } from './core/components/gestion-niveles/gestion-niveles.component';
import { GestionMateriasComponent } from './core/components/gestion-materias/gestion-materias.component';
import { GestionAsignacionDictaComponent } from './core/components/gestion-asignacion-dicta/gestion-asignacion-dicta.component';

// --- 1. Importar el Guardián ---
// (Asegúrate que la ruta a tu guardián funcional sea correcta)
import { authGuard } from './guards/auth.guard';
import { GestionInscripcionComponent } from './core/components/gestion-inscripcion/gestion-inscripcion.component';
import { GestionItemsComponent } from './core/components/gestion-items/gestion-items.component';
import { GestionModalidadComponent } from './core/components/gestion-modalidad/gestion-modalidad.component';

const routes: Routes = [
  // --- Rutas Públicas (para "Invitados") ---
  { path: 'home', component: HomeComponent },
  { path: 'cont', component: ContactosComponent },
  // (Asumo que tienes un componente de Login que también es público)
  // { path: 'login', component: LoginComponent },


  // --- Rutas Protegidas (Requieren Login y Roles) ---
  {
    path: 'UserGes',
    component: GestionUsuariosComponent,
    canActivate: [authGuard], // 👈 Protege la ruta
    data: { roles: ['Administrador'] } // 👈 Define los roles requeridos
  },
  {
    path: 'Roles',
    component: GestionRolesComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'Menus',
    component: GestionMenusComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'AsignacionProcesosMenus',
    component: GestionAsignacionComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'AsignacionRolesMenus',
    component: GestionAsignacionRolesComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'AsignacionRolesUsuarios',
    component: GestionAsignacionRolesUsuariosComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'paralelos',
    component: GestionParalelosComponent,
    canActivate: [authGuard],
    // Ejemplo: Permitir a Administradores Y Docentes
    data: { roles: ['Administrador', 'Docente'] }
  },
  {
    path: 'niveles',
    component: GestionNivelesComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'items',
    component: GestionItemsComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },

  {
    path: 'materias',
    component: GestionMateriasComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'asignardicta',
    component: GestionAsignacionDictaComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'inscripcionAlumnos',
    component: GestionInscripcionComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  {
    path: 'modalidad',
    component: GestionModalidadComponent,
    canActivate: [authGuard],
    data: { roles: ['Administrador'] }
  },
  // --- Redirecciones ---
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // Ruta por defecto
  { path: '**', redirectTo: '/home' } // 👈 AÑADIDO: Wildcard para "Not Found"
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
