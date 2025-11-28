package com.Proyecto.backEnd.service;

import java.util.List;
import java.util.Set; // ✅ Importar
import java.util.stream.Collectors; // ✅ Importar

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.model.MenusModel; // ✅ Importar
import com.Proyecto.backEnd.model.ProcesosModel;
import com.Proyecto.backEnd.repository.MenusRepo; // ✅ Importar
import com.Proyecto.backEnd.repository.ProcesosRepo;

// ✅ Imports para paginación y filtros
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join; // ✅ Importar
import jakarta.persistence.criteria.Root; // ✅ Importar
import jakarta.persistence.criteria.Subquery; // ✅ Importar


@Service
public class ProcesosService {
    
    @Autowired
    ProcesosRepo proRepo;

    @Autowired
    MenusRepo menRepo; // ✅ Necesario para la lógica de asignación

    /**
     * ✅ B-7. Lógica de listado, filtrado (por nombre) y filtrado de asignación.
     * Este es el método más complejo.
     */
    public Page<ProcesosModel> getProcesosParaMenu(int codm, String filtro, String asignado, Pageable pageable) {
        
        // 1. Especificación base (filtro de nombre y estado)
        Specification<ProcesosModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction(); // WHERE 1=1

            // Filtro por nombre
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            // Filtro por estado (generalmente queremos ver solo procesos activos para asignar)
            p = cb.and(p, cb.equal(root.get("estado"), 1));


            // 2. Subquery para filtrar por asignación ("SI" o "NO") [cite: 768, 769]
            if (!"TODOS".equals(asignado)) {
                
                // SELECT p.codp FROM procesos p JOIN mepro mp ON p.codp = mp.codp WHERE mp.codm = ?
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<MenusModel> menuRoot = subquery.from(MenusModel.class);
                Join<MenusModel, ProcesosModel> menuProcesos = menuRoot.join("procesos");
                subquery.select(menuProcesos.get("codp")).where(cb.equal(menuRoot.get("codm"), codm));

                if ("SI".equals(asignado)) {
                    p = cb.and(p, root.get("codp").in(subquery)); // WHERE codp IN (subquery)
                } else if ("NO".equals(asignado)) {
                    p = cb.and(p, root.get("codp").in(subquery).not()); // WHERE codp NOT IN (subquery)
                }
            }
            return p;
        };

        // 3. Ejecutar la consulta
        Page<ProcesosModel> pagina = proRepo.findAll(spec, pageable);

        // 4. Marcar los flags "asignado" para el frontend [cite: 777]
        // (Esto es necesario para el caso "TODOS", donde la consulta trae mezclados)
        MenusModel menu = menRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Menu no encontrado"));
        
        Set<Integer> idsAsignados = menu.getProcesos().stream()
                .map(ProcesosModel::getCodp)
                .collect(Collectors.toSet());

        pagina.getContent().forEach(p -> p.setAsignado(idsAsignados.contains(p.getCodp())));

        return pagina;
    }

    // (Tu método original, ahora actualizado para paginación simple)
    public Page<ProcesosModel> listaProcesos(String filtro, String estado, Pageable pageable){
        Specification<ProcesosModel> spec = (root, query, cb) -> {
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
        return proRepo.findAll(spec, pageable);
    }
}