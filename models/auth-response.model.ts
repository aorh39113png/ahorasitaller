// 1. Nueva interfaz para Proceso
export interface Proceso {
  codp?: number;
  nombre: string;
  estado?: number;
  asignado: boolean; // Flag para el checkbox
}
export interface Paralelo {
  codpar?: number;
  nombre: string;
  estado?: number;
}
export interface Materia {
  codmat: string; // La sigla (PK)
  nombre: string;
  estado?: number;

  // El backend nos enviará el objeto Nivel completo
  // Para enviar, solo necesitaremos { "codn": ... }
  nivel: {
    codn: number;
    nombre?: string; // Opcional, solo para mostrar
  };

  // (Opcional) Listas que el backend podría enviar
  paralelos?: Paralelo[];
  items?: any[]; // Reemplaza 'any' si tienes una interfaz 'Item'
}
// 1. Interfaz para Menú
export interface Menu {
  codm?: number;
  nombre: string;
  estado?: number;
  asignado?: boolean;
}
export interface Gestion {
  gestion: number;
}
// 2. Interfaz para la respuesta paginada (reutilizable)
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

// 1. Definimos la interfaz para el Rol
export interface Role {
  codr?: number;
  nombre: string;
  estado?: number;
  asignado?: boolean;

}
export interface Item {
  codi?: number;
  nombre: string;
  estado?: number;
}
export interface Mapa {
  id: {
    codmat: string;
    codpar: number;
    gestion: number;
  };
  // Necesitamos el objeto 'paralelo' para mostrar el nombre
  paralelo: {
    codpar: number;
    nombre: string;
  };
  estado: number;
}
export interface AuthResponse {
  token: string;
  nombre: string;
  roles: Role[];
  fecha: string;
}
export interface Nivel {
  codn?: number;
  nombre: string;
  estado?: number;
}
export interface Usuario {
  login: string;
  estado?: number;
  personal: { // We only need the name for display
    codp: number;
    nombre: string;
    ap: string;
    am?: string;
  };

}
// ... (tus interfaces existentes: PageResponse, Nivel, Paralelo, Materia, Mapa, Gestion, etc.)

// Interfaz para el dropdown de Profesores (B-16.1)
export interface Profesor {
  codp: number;
  nombre: string;
  ap: string;
  am?: string;
}

// Interfaz para el dropdown de "Nivel-Materia-Paralelo" (B-16.1)
// Es el MapaModel pero con las relaciones cargadas
export interface MapaActivo {
  id: {
    codmat: string;
    codpar: number;
    gestion: number;
  };
  materia: {
    nombre: string;
    nivel: {
      nombre: string;
    };
  };
  paralelo: {
    nombre: string;
  };
}

// Interfaz para la tabla principal (B-16)
export interface Dicta {
  id: {
    codpar: number;
    codp: number;
    codmat: string;
    gestion: number;
  };
  estado: number;
  materia: Materia;
  paralelo: Paralelo;
  profesor: Profesor;
  usuario: {
    login: string;
  };

}

export interface Estudiante {
  codp: number;
  nombre: string;
  ap: string;
  am?: string;
}

export interface Progra {
  id: {
    codpar: number;
    codp: number;
    codmat: string;
    gestion: number;
  };
  estado: number;
  materia: Materia;
  paralelo: Paralelo;
  alumno: Estudiante; // Usamos 'alumno' para diferenciar
  usuario: { login: string };
}
// Interfaz para B-14
export interface Modalidad {
  codm?: number;
  nombre: string;
  estado?: number;
}

// Interfaz para B-15
export interface DModalidad {
  coddm: string; // Es String según tu backend
  nombre: string;
  estado?: number;
  modalidad: {
    codm: number;
    nombre?: string;
  };
}