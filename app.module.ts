

import { NgModule, LOCALE_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './core/components/home/home.component';
import { ContactosComponent } from './core/components/contactos/contactos.component';
import { MenuComponent } from './core/components/menu/menu.component';
import { GestionUsuariosComponent } from './core/components/gestion-usuarios/gestion-usuarios.component';
import { ModalAccesoComponent } from './core/components/gestion-usuarios/modal-acceso/modal-acceso.component';
import { ModalConfirmacionComponent } from './core/components/gestion-usuarios/modal-confirmacion/modal-confirmacion.component';
import { ModalPersonaComponent } from './core/components/gestion-usuarios/modal-persona/modal-persona.component';
import { provideHttpClient } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { GestionRolesComponent } from './core/components/gestion-roles/gestion-roles.component';
import { ModalRolComponent } from './core/modales/modalrol/modalrol.component';
import { GestionMenusComponent } from './core/components/gestion-menus/gestion-menus.component';
import { ModalMenuComponent } from './core/modales/modal-menu/modal-menu.component';
import { GestionAsignacionComponent } from './core/components/gestion-asignacion/gestion-asignacion.component';
import { GestionAsignacionRolesComponent } from './core/components/gestion-asignacion-roles/gestion-asignacion-roles.component';
import { GestionAsignacionRolesUsuariosComponent } from './core/components/gestion-asignacion-roles-usuarios/gestion-asignacion-roles-usuarios.component';
import { GestionParalelosComponent } from './core/components/gestion-paralelos/gestion-paralelos.component';
import { ModalParaleloComponent } from './core/modales/modal-paralelo/modal-paralelo.component';
import { ModalNivelComponent } from './core/modales/modal-nivel/modal-nivel.component';
import { GestionNivelesComponent } from './core/components/gestion-niveles/gestion-niveles.component';
import { GestionMateriasComponent } from './core/components/gestion-materias/gestion-materias.component';
import { ModalMateriaComponent } from './core/modales/modal-materia/modal-materia.component';
import { ModalAsignacionDictaComponent } from './core/modales/modal-asignacion-dicta/modal-asignacion-dicta.component';
import { GestionAsignacionDictaComponent } from './core/components/gestion-asignacion-dicta/gestion-asignacion-dicta.component';
import { NotificationComponent } from './core/components/notification/notification.component';
import { ModalInscripcionComponent } from './core/modales/modal-inscripcion/modal-inscripcion.component';
import { GestionInscripcionComponent } from './core/components/gestion-inscripcion/gestion-inscripcion.component';
import { GestionItemsComponent } from './core/components/gestion-items/gestion-items.component';
import { ModalItemsComponent } from './core/modales/modal-items/modal-items.component';
import { ModalModalidadComponent } from './core/modales/modal-modalidad/modal-modalidad.component';
import { GestionModalidadComponent } from './core/components/gestion-modalidad/gestion-modalidad.component';

// 🔹 Registrar el idioma español globalmente
registerLocaleData(localeEs, 'es');

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ContactosComponent,
    MenuComponent,
    GestionUsuariosComponent,
    ModalAccesoComponent,
    ModalConfirmacionComponent,
    ModalPersonaComponent,
    GestionRolesComponent,
    ModalRolComponent,
    GestionMenusComponent,
    ModalMenuComponent,
    GestionAsignacionComponent,
    GestionAsignacionRolesComponent,
    GestionAsignacionRolesUsuariosComponent,
    GestionParalelosComponent,
    ModalParaleloComponent,
    ModalNivelComponent,
    GestionNivelesComponent,
    GestionMateriasComponent,
    ModalMateriaComponent,
    ModalAsignacionDictaComponent,
    GestionAsignacionDictaComponent,
    NotificationComponent,
    ModalInscripcionComponent,
    GestionInscripcionComponent,
    GestionItemsComponent,
    ModalItemsComponent,
    ModalModalidadComponent,
    GestionModalidadComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  providers: [
    provideHttpClient(),
    { provide: LOCALE_ID, useValue: 'es' }
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
