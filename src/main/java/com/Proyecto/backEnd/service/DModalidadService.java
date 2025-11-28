package com.Proyecto.backEnd.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Proyecto.backEnd.model.DModalidadModel;
import com.Proyecto.backEnd.repository.DModalidadRepo;
import jakarta.persistence.criteria.Predicate;

@Service
public class DModalidadService {

    @Autowired
    DModalidadRepo dModalidadRepo;

    // Listar Paginado
    public Page<DModalidadModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<DModalidadModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            return p;
        };
        return dModalidadRepo.findAll(spec, pageable);
    }

    public DModalidadModel crear(DModalidadModel m) {
        m.setEstado(1);
        return dModalidadRepo.save(m);
    }

    public DModalidadModel modificar(String coddm, DModalidadModel datos) {
        DModalidadModel m = dModalidadRepo.findById(coddm)
                .orElseThrow(() -> new RuntimeException("Detalle Modalidad no encontrada"));
        m.setNombre(datos.getNombre());
        m.setModalidad(datos.getModalidad());
        return dModalidadRepo.save(m);
    }

    public void eliminar(String coddm) {
        DModalidadModel m = dModalidadRepo.findById(coddm)
                .orElseThrow(() -> new RuntimeException("Detalle Modalidad no encontrada"));
        m.setEstado(0);
        dModalidadRepo.save(m);
    }

    public void habilitar(String coddm) {
        DModalidadModel m = dModalidadRepo.findById(coddm)
                .orElseThrow(() -> new RuntimeException("Detalle Modalidad no encontrada"));
        m.setEstado(1);
        dModalidadRepo.save(m);
    }

    public DModalidadModel buscarPorId(String coddm) {
        return dModalidadRepo.findById(coddm)
                .orElseThrow(() -> new RuntimeException("Detalle Modalidad no encontrada"));
    }

    public List<DModalidadModel> listarActivos() {
        Specification<DModalidadModel> spec = (root, query, cb) -> cb.equal(root.get("estado"), 1);
        return dModalidadRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"));
    }
}