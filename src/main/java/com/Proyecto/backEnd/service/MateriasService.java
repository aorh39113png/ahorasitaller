package com.Proyecto.backEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Proyecto.backEnd.model.MateriasModel;
import com.Proyecto.backEnd.model.NivelesModel;
import com.Proyecto.backEnd.repository.MateriasRepo;
import com.Proyecto.backEnd.repository.NivelesRepo;
import jakarta.persistence.criteria.Predicate;

@Service
public class MateriasService {

    @Autowired
    MateriasRepo materiasRepo;

    @Autowired
    NivelesRepo nivelesRepo; // Para adjuntar el nivel

    /**
     * B-12. Listar, filtrar y paginar
     */
    public Page<MateriasModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<MateriasModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            
            // Cargar la relación 'nivel' para evitar N+1 queries
            root.fetch("nivel"); 
            query.distinct(true);

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
        return materiasRepo.findAll(spec, pageable);
    }

    /**
     * B-12.3. Adicionar Nueva Materia
     */
    public MateriasModel crearMateria(MateriasModel materia) {
        // 1. Validar que la Sigla (PK) no exista
        if (materiasRepo.existsById(materia.getCodmat())) {
            throw new RuntimeException("La sigla (codmat) '" + materia.getCodmat() + "' ya existe.");
        }

        // 2. Buscar y adjuntar el Nivel (el frontend solo envía el codn)
        int codn = materia.getNivel().getCodn();
        NivelesModel nivel = nivelesRepo.findById(codn)
            .orElseThrow(() -> new RuntimeException("Nivel no encontrado con id: " + codn));
        
        materia.setNivel(nivel);
        materia.setEstado(1); // Activo por defecto
        return materiasRepo.save(materia);
    }

    /**
     * B-12.4. Modificar Datos
     */
    public MateriasModel modificarMateria(String codmat, MateriasModel datos) {
        MateriasModel m = materiasRepo.findById(codmat)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con sigla: " + codmat));

        // 1. Buscar y adjuntar el Nivel
        int codn = datos.getNivel().getCodn();
        NivelesModel nivel = nivelesRepo.findById(codn)
            .orElseThrow(() -> new RuntimeException("Nivel no encontrado con id: " + codn));
        
        m.setNombre(datos.getNombre());
        m.setNivel(nivel);
        // La sigla (codmat) no se puede cambiar
        
        return materiasRepo.save(m);
    }
    
    /**
     * B-12.5. Eliminar (Baja lógica)
     */
    public void eliminarLogico(String codmat) {
        MateriasModel m = materiasRepo.findById(codmat).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        m.setEstado(0);
        materiasRepo.save(m);
    }

    /**
     * B-12.6. Habilitar
     */
    public void habilitar(String codmat) {
        MateriasModel m = materiasRepo.findById(codmat).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        m.setEstado(1);
        materiasRepo.save(m);
    }

    /**
     * Auxiliar para buscar por ID
     */
    public MateriasModel buscarPorId(String codmat) {
         return materiasRepo.findById(codmat).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }
}