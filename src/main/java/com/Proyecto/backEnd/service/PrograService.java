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
public class PrograService {

    @Autowired
    private PrograRepo prograRepo;
    @Autowired
    private UsuariosRepo usuariosRepo;
    @Autowired
    private MateriasRepo materiasRepo;
    @Autowired
    private ParalelosRepo paralelosRepo;
    @Autowired
    private PersonalRepo personalRepo;

    /**
     * B-17. Listar, filtrar y paginar Inscripciones
     */
    public Page<PrograModel> listarPaginado(String filtro, String estado, Integer codn, Pageable pageable) {

        Specification<PrograModel> spec = (root, query, cb) -> {
            root.fetch("materia").fetch("nivel");
            root.fetch("alumno");
            root.fetch("paralelo");
            query.distinct(true);

            Predicate p = cb.conjunction();

            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }

            if (codn != null && codn > 0) {
                Join<PrograModel, MateriasModel> materiaJoin = root.join("materia");
                Join<MateriasModel, NivelesModel> nivelJoin = materiaJoin.join("nivel");
                p = cb.and(p, cb.equal(nivelJoin.get("codn"), codn));
            }

            if (filtro != null && !filtro.isEmpty()) {
                String filtroLower = "%" + filtro.toLowerCase() + "%";

                Join<PrograModel, PersonalModel> alumnoJoin = root.join("alumno");
                Join<PrograModel, MateriasModel> materiaJoin = root.join("materia", JoinType.LEFT);

                Predicate filtroAlumno = cb.or(
                        cb.like(cb.lower(alumnoJoin.get("nombre")), filtroLower),
                        cb.like(cb.lower(alumnoJoin.get("ap")), filtroLower));
                Predicate filtroMateria = cb.like(cb.lower(materiaJoin.get("nombre")), filtroLower);

                p = cb.and(p, cb.or(filtroAlumno, filtroMateria));
            }
            return p;
        };
        return prograRepo.findAll(spec, pageable);
    }

    /**
     * B-17.1. Crear Inscripción
     */
    public PrograModel crearInscripcion(String codmat, int codpar, int codp, int gestion, String login) {

        PrograId id = new PrograId(codpar, codp, codmat, gestion);
        if (prograRepo.existsById(id)) {
            return null; // Ya existe
        }

        MateriasModel mat = materiasRepo.findById(codmat)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        ParalelosModel par = paralelosRepo.findById(codpar)
                .orElseThrow(() -> new RuntimeException("Paralelo no encontrado"));
        PersonalModel alum = personalRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        UsuariosModel usu = usuariosRepo.findById(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PrograModel nueva = new PrograModel();
        nueva.setId(id);
        nueva.setMateria(mat);
        nueva.setParalelo(par);
        nueva.setAlumno(alum);
        nueva.setUsuario(usu);
        nueva.setEstado(1);

        return prograRepo.save(nueva);
    }

    /**
     * B-17.2. Modificar Inscripción (Delete + Create)
     */
    @Transactional
    public PrograModel modificarInscripcion(
            String oldCodmat, int oldCodpar, int oldCodp, int oldGestion,
            String newCodmat, int newCodpar, int newCodp, String login) {

        PrograId idViejo = new PrograId(oldCodpar, oldCodp, oldCodmat, oldGestion);
        if (!prograRepo.existsById(idViejo)) {
            throw new RuntimeException("No se encontró la inscripción original.");
        }
        prograRepo.deleteById(idViejo);

        PrograModel nueva = this.crearInscripcion(newCodmat, newCodpar, newCodp, oldGestion, login);
        if (nueva == null) {
            throw new RuntimeException("La nueva inscripción ya existe.");
        }
        return nueva;
    }

    public void eliminarLogico(PrograId id) {
        PrograModel p = prograRepo.findById(id).orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        p.setEstado(0);
        prograRepo.save(p);
    }

    public void habilitar(PrograId id) {
        PrograModel p = prograRepo.findById(id).orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        p.setEstado(1);
        prograRepo.save(p);
    }
}