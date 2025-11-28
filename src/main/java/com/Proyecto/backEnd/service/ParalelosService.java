package com.Proyecto.backEnd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.exception.ResourceNotFoundException;
import com.Proyecto.backEnd.model.ParalelosModel;
import com.Proyecto.backEnd.repository.ParalelosRepo;

import jakarta.persistence.criteria.Predicate;

@Service
public class ParalelosService {

    @Autowired
    ParalelosRepo paralelosRepo;

    /**
     * B-10. Listar, filtrar (por nombre y estado) y paginar
     */
    public Page<ParalelosModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<ParalelosModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            // Filtro por nombre [cite: 864]
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            // Filtro por estado [cite: 865]
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            // "TODOS" no aplica filtro

            return p;
        };
        return paralelosRepo.findAll(spec, pageable);
    }

    /**
     * B-10.1. Adicionar Nuevo Paralelo [cite: 875]
     */
    public ParalelosModel crearParalelo(ParalelosModel paralelo) {
        if (paralelosRepo.existsByNombreIgnoreCase(paralelo.getNombre())) {
            throw new DuplicateResourceException("Ya existe un paralelo con el nombre '" + paralelo.getNombre() + "'.");
        }
        paralelo.setEstado(1); // Se crea como Activo por defecto
        return paralelosRepo.save(paralelo);
    }

    /**
     * B-10.2. Modificar Datos del Paralelo [cite: 897]
     */
    public ParalelosModel modificarParalelo(int codpar, ParalelosModel datos) {
        ParalelosModel p = paralelosRepo.findById(codpar)
            .orElseThrow(() -> new ResourceNotFoundException("Paralelo no encontrado con id: " + codpar));

        if (paralelosRepo.existsByNombreIgnoreCaseAndCodparNot(datos.getNombre(), codpar)) {
            throw new DuplicateResourceException("Ya existe un paralelo con el nombre '" + datos.getNombre() + "'.");
        }

        p.setNombre(datos.getNombre());
        return paralelosRepo.save(p);
    }

    /**
     * B-10.3. Eliminar Paralelo (Baja lógica) [cite: 925]
     */
    public void eliminarLogico(int codpar) {
        ParalelosModel p = paralelosRepo.findById(codpar)
            .orElseThrow(() -> new ResourceNotFoundException("Paralelo no encontrado con id: " + codpar));
        p.setEstado(0); // Damos de baja
        paralelosRepo.save(p);
    }

    /**
     * B-10.4. Habilitar Paralelo [cite: 938]
     */
    public void habilitar(int codpar) {
        ParalelosModel p = paralelosRepo.findById(codpar)
            .orElseThrow(() -> new ResourceNotFoundException("Paralelo no encontrado con id: " + codpar));
        p.setEstado(1); // Reactivamos
        paralelosRepo.save(p);
    }

    /**
     * Método auxiliar para buscar por ID (para el modal de modificar)
     */
    public ParalelosModel buscarPorId(int codpar) {
         return paralelosRepo.findById(codpar)
            .orElseThrow(() -> new ResourceNotFoundException("Paralelo no encontrado con id: " + codpar));
    }

    // ... (listarPaginado, crearParalelo, y otros métodos existentes) ...

    /**
     * Método auxiliar para el dropdown de "Asignar Paralelo" (B-12.1).
     * Devuelve solo los paralelos activos.
     */
    public List<ParalelosModel> listarTodosActivos() {
        Specification<ParalelosModel> spec = (root, query, cb) ->
            cb.equal(root.get("estado"), 1); // Solo activos
        return paralelosRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"));
    }
}