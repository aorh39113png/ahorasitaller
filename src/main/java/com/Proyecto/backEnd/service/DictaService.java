package com.Proyecto.backEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Proyecto.backEnd.model.*;
import com.Proyecto.backEnd.repository.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

@Service
public class DictaService {

    @Autowired
    private DictaRepo dictaRepo;
    @Autowired
    private UsuariosRepo usuariosRepo;
    @Autowired
    private MateriasRepo materiasRepo;
    @Autowired
    private ParalelosRepo paralelosRepo;
    @Autowired
    private PersonalRepo personalRepo;

    /**
     * B-16. Listar, filtrar y paginar
     */
    public Page<DictaModel> listarPaginado(String filtro, String estado, Integer codn, Pageable pageable) {
        
        Specification<DictaModel> spec = (root, query, cb) -> {
            // Hacemos fetch para cargar las entidades relacionadas y evitar N+1 queries
            root.fetch("materia").fetch("nivel");
            root.fetch("profesor");
            root.fetch("paralelo");
            query.distinct(true);
            
            Predicate p = cb.conjunction();

            // Filtro por Estado (Activos/Bajas/Todos)
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }

            // Filtro por Nivel (codn)
            if (codn != null && codn > 0) {
                Join<DictaModel, MateriasModel> materiaJoin = root.join("materia");
                Join<MateriasModel, NivelesModel> nivelJoin = materiaJoin.join("nivel");
                p = cb.and(p, cb.equal(nivelJoin.get("codn"), codn));
            }

            // Filtro por Texto (Nombre/Apellido de Profesor O Nombre de Materia)
            if (filtro != null && !filtro.isEmpty()) {
                String filtroLower = "%" + filtro.toLowerCase() + "%";
                
                Join<DictaModel, PersonalModel> profesorJoin = root.join("profesor");
                Join<DictaModel, MateriasModel> materiaJoin = root.join("materia", JoinType.LEFT); // Usamos LEFT por si el join ya existía

                Predicate filtroProfesor = cb.or(
                    cb.like(cb.lower(profesorJoin.get("nombre")), filtroLower),
                    cb.like(cb.lower(profesorJoin.get("ap")), filtroLower)
                );
                Predicate filtroMateria = cb.like(cb.lower(materiaJoin.get("nombre")), filtroLower);

                p = cb.and(p, cb.or(filtroProfesor, filtroMateria));
            }
            
            return p;
        };
        
        return dictaRepo.findAll(spec, pageable);
    }

    /**
     * B-16.1. Adicionar Nueva Asignación
     */
    public DictaModel crearAsignacion(String codmat, int codpar, int codp, int gestion, String login) {
        
        DictaId id = new DictaId(codpar, codp, codmat, gestion);
        if (dictaRepo.existsById(id)) {
            throw new RuntimeException("Esta asignación (Profesor-Materia-Paralelo) ya existe para esta gestión.");
        }

        // Buscamos las entidades para validarlas
        MateriasModel mat = materiasRepo.findById(codmat).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        ParalelosModel par = paralelosRepo.findById(codpar).orElseThrow(() -> new RuntimeException("Paralelo no encontrado"));
        PersonalModel prof = personalRepo.findById(codp).orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        UsuariosModel usu = usuariosRepo.findById(login).orElseThrow(() -> new RuntimeException("Usuario (login) no encontrado"));

        DictaModel nueva = new DictaModel();
        nueva.setId(id);
        nueva.setMateria(mat);
        nueva.setParalelo(par);
        nueva.setProfesor(prof);
        nueva.setUsuario(usu);
        nueva.setEstado(1); // Activo por defecto

        return dictaRepo.save(nueva);
    }
    
    /**
     * B-16.3. Eliminar Asignación (Baja lógica)
     */
    public void eliminarLogico(DictaId id) {
        DictaModel d = dictaRepo.findById(id).orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        d.setEstado(0); // Damos de baja
        dictaRepo.save(d);
    }

    /**
     * Habilitar Asignación (similar a B-16.3)
     */
    public void habilitar(DictaId id) {
        DictaModel d = dictaRepo.findById(id).orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        d.setEstado(1); // Reactivamos
        dictaRepo.save(d);
    }
    
    @Transactional
    public DictaModel modificarAsignacion(
            // --- Clave Vieja (para borrar)
            String oldCodmat, int oldCodpar, int oldCodp, int oldGestion,
            // --- Clave Nueva (para crear)
            String newCodmat, int newCodpar, int newCodp, String login) {
        
        // 1. Crear el ID viejo y borrar la asignación anterior
        DictaId idViejo = new DictaId(oldCodpar, oldCodp, oldCodmat, oldGestion);
        if (!dictaRepo.existsById(idViejo)) {
            throw new RuntimeException("No se encontró la asignación original para modificar.");
        }
        dictaRepo.deleteById(idViejo);

        // 2. Crear la nueva asignación usando el método que ya teníamos
        // (La gestión 'oldGestion' se reutiliza como la gestión actual)
        return this.crearAsignacion(newCodmat, newCodpar, newCodp, oldGestion, login);
    }
}