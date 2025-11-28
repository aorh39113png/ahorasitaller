package com.Proyecto.backEnd.service;

import java.util.Set; // ✅ Importar
import java.util.stream.Collectors; // ✅ Importar

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.exception.ResourceNotFoundException;
import com.Proyecto.backEnd.model.MenusModel;
import com.Proyecto.backEnd.model.RolesModel; // ✅ Importar
import com.Proyecto.backEnd.repository.MenusRepo;
import com.Proyecto.backEnd.repository.RolesRepo; // ✅ Importar

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join; // ✅ Importar
import jakarta.persistence.criteria.Root; // ✅ Importar
import jakarta.persistence.criteria.Subquery; // ✅ Importar

@Service
public class MenusService {

    @Autowired
    MenusRepo menRepo;
    @Autowired
    RolesRepo rolRepo;

    public Page<MenusModel> getMenusParaRol(int codr, String filtro, String asignado, Pageable pageable) {

        // 1. Especificación base (filtro de nombre y estado activo)
        Specification<MenusModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction(); // WHERE 1=1

            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }
            p = cb.and(p, cb.equal(root.get("estado"), 1)); // Solo menús activos

            // 2. Subquery para filtrar por asignación ("SI" o "NO")
            if (!"TODOS".equals(asignado)) {
                // SELECT m.codm FROM menus m JOIN rolme rm ON m.codm = rm.codm WHERE rm.codr = ?
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<RolesModel> rolRoot = subquery.from(RolesModel.class);
                Join<RolesModel, MenusModel> rolMenus = rolRoot.join("menus");
                subquery.select(rolMenus.get("codm")).where(cb.equal(rolRoot.get("codr"), codr));

                if ("SI".equals(asignado)) {
                    p = cb.and(p, root.get("codm").in(subquery)); // WHERE codm IN (subquery)
                } else if ("NO".equals(asignado)) {
                    p = cb.and(p, root.get("codm").in(subquery).not()); // WHERE codm NOT IN (subquery)
                }
            }
            return p;
        };

        // 3. Ejecutar la consulta
        Page<MenusModel> pagina = menRepo.findAll(spec, pageable);

        // 4. Marcar los flags "asignado" para el frontend
        RolesModel rol = rolRepo.findById(codr)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        Set<Integer> idsAsignados = rol.getMenus().stream()
                .map(MenusModel::getCodm)
                .collect(Collectors.toSet());

        pagina.getContent().forEach(m -> m.setAsignado(idsAsignados.contains(m.getCodm())));

        return pagina;
    }
    /**
     * ✅ B-6. Lista, filtra y pagina los Menús.
     */
    public Page<MenusModel> listarPaginado(String filtro, String estado, Pageable pageable) {

        Specification<MenusModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction(); // WHERE 1=1

            // 1. Filtro por nombre
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            // 2. Filtro por estado
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            // "TODOS" no añade filtro
            return p;
        };
        return menRepo.findAll(spec, pageable);
    }

    /**
     * ✅ B-6.1. Adicionar Nuevo Menú
     */
    public MenusModel crearMenu(MenusModel menu) {
        if (menRepo.existsByNombreIgnoreCase(menu.getNombre())) {
            throw new DuplicateResourceException("Ya existe un menú con el nombre '" + menu.getNombre() + "'.");
        }
        menu.setEstado(1); // Activo por defecto
        return menRepo.save(menu);
    }

    /**
     * ✅ B-6.2. Modificar Datos del Menú
     */
    public MenusModel modificarMenu(int codm, MenusModel menuDatos) {
        MenusModel menu = menRepo.findById(codm)
            .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado"));

        if (menRepo.existsByNombreIgnoreCaseAndCodmNot(menuDatos.getNombre(), codm)) {
            throw new DuplicateResourceException("Ya existe un menú con el nombre '" + menuDatos.getNombre() + "'.");
        }

        menu.setNombre(menuDatos.getNombre());
        return menRepo.save(menu);
    }

    /**
     * ✅ B-6.3. Eliminar (Baja lógica)
     */
    public void eliminarLogico(int codm) {
        MenusModel menu = menRepo.findById(codm)
            .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado"));
        menu.setEstado(0);
        menRepo.save(menu);
    }

    /**
     * ✅ B-6.4. Habilitar Menú
     */
    public void habilitarMenu(int codm) {
        MenusModel menu = menRepo.findById(codm)
            .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado"));
        menu.setEstado(1);
        menRepo.save(menu);
    }

    // (Helper) Buscar por ID
    public MenusModel buscarPorId(int codm) {
        return menRepo.findById(codm)
            .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado"));
    }
}