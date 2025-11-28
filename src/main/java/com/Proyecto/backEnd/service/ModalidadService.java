package com.Proyecto.backEnd.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Proyecto.backEnd.model.ModalidadModel;
import com.Proyecto.backEnd.repository.ModalidadRepo;
import jakarta.persistence.criteria.Predicate;

@Service
public class ModalidadService {

    @Autowired
    ModalidadRepo modalidadRepo;

    // Listar Paginado
    public Page<ModalidadModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<ModalidadModel> spec = (root, query, cb) -> {
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
        return modalidadRepo.findAll(spec, pageable);
    }

    public ModalidadModel crear(ModalidadModel m) {
        if (modalidadRepo.existsByNombre(m.getNombre())) {
            throw new RuntimeException("El nombre de la modalidad ya existe.");
        }
        m.setEstado(1);
        return modalidadRepo.save(m);
    }

    public ModalidadModel modificar(int codm, ModalidadModel datos) {
        ModalidadModel m = modalidadRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        if (modalidadRepo.existsByNombreAndCodmNot(datos.getNombre(), codm)) {
            throw new RuntimeException("El nombre de la modalidad ya existe.");
        }

        m.setNombre(datos.getNombre());
        return modalidadRepo.save(m);
    }

    public void eliminar(int codm) {
        ModalidadModel m = modalidadRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
        m.setEstado(0);
        modalidadRepo.save(m);
    }

    public void habilitar(int codm) {
        ModalidadModel m = modalidadRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
        m.setEstado(1);
        modalidadRepo.save(m);
    }

    public ModalidadModel buscarPorId(int codm) {
        return modalidadRepo.findById(codm).orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
    }

    // Para el dropdown de B-15
    public List<ModalidadModel> listarActivos() {
        Specification<ModalidadModel> spec = (root, query, cb) -> cb.equal(root.get("estado"), 1);
        return modalidadRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"));
    }
}