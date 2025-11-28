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
import com.Proyecto.backEnd.model.NivelesModel;
import com.Proyecto.backEnd.repository.NivelesRepo;
import jakarta.persistence.criteria.Predicate;

@Service
public class NivelesService {

    @Autowired
    NivelesRepo nivelesRepo;

    /**
     * B-13. Listar, filtrar (por nombre y estado) y paginar
     */
    public Page<NivelesModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<NivelesModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            // Filtro por nombre [cite: 1113]
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            // Filtro por estado [cite: 1114]
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            // "TODOS" no aplica filtro

            return p;
        };
        return nivelesRepo.findAll(spec, pageable);
    }

    /**
     * B-13.1. Adicionar Nuevo Nivel [cite: 1131]
     */
    public NivelesModel crearNivel(NivelesModel nivel) {
        if (nivelesRepo.existsByNombreIgnoreCase(nivel.getNombre())) {
            throw new DuplicateResourceException("Ya existe un nivel con el nombre '" + nivel.getNombre() + "'.");
        }
        nivel.setEstado(1); // Se crea como Activo por defecto
        return nivelesRepo.save(nivel);
    }

    /**
     * B-13.2. Modificar Datos del Nivel [cite: 1151]
     */
    public NivelesModel modificarNivel(int codn, NivelesModel datos) {
        NivelesModel n = nivelesRepo.findById(codn)
            .orElseThrow(() -> new ResourceNotFoundException("Nivel no encontrado con id: " + codn));

        if (nivelesRepo.existsByNombreIgnoreCaseAndCodnNot(datos.getNombre(), codn)) {
            throw new DuplicateResourceException("Ya existe un nivel con el nombre '" + datos.getNombre() + "'.");
        }

        n.setNombre(datos.getNombre());
        return nivelesRepo.save(n);
    }

    /**
     * B-13.3. Eliminar Nivel (Baja lógica) [cite: 1169]
     */
    public void eliminarLogico(int codn) {
        NivelesModel n = nivelesRepo.findById(codn)
            .orElseThrow(() -> new ResourceNotFoundException("Nivel no encontrado con id: " + codn));
        n.setEstado(0); // Damos de baja
        nivelesRepo.save(n);
    }

    /**
     * B-13.4. Habilitar Nivel [cite: 1179]
     */
    public void habilitar(int codn) {
        NivelesModel n = nivelesRepo.findById(codn)
            .orElseThrow(() -> new ResourceNotFoundException("Nivel no encontrado con id: " + codn));
        n.setEstado(1); // Reactivamos
        nivelesRepo.save(n);
    }

    /**
     * Método auxiliar para buscar por ID
     */
    public NivelesModel buscarPorId(int codn) {
         return nivelesRepo.findById(codn)
            .orElseThrow(() -> new ResourceNotFoundException("Nivel no encontrado con id: " + codn));
    }

    /**
     * Método auxiliar para el dropdown de "Gestión Materias" (B-12).
     * Devuelve solo los niveles activos. [cite: 1032]
     */
    public List<NivelesModel> listarTodosActivos() {
        Specification<NivelesModel> spec = (root, query, cb) ->
            cb.equal(root.get("estado"), 1); // Solo activos
        return nivelesRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"));
    }
}