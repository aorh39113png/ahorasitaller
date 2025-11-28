package com.Proyecto.backEnd.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.exception.ResourceNotFoundException;
import com.Proyecto.backEnd.model.RolesModel;
import com.Proyecto.backEnd.model.UsuariosModel;
import com.Proyecto.backEnd.repository.RolesRepo;
import com.Proyecto.backEnd.repository.UsuariosRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class RolesService {
    @Autowired
    RolesRepo rolRepo;
    @Autowired
    UsuariosRepo usuRepo; // ✅ Inyectar UsuariosRepo

    /**
     * ✅ B-9. Lógica de listado, filtrado (por nombre) y filtrado de asignación.
     */
    public Page<RolesModel> getRolesParaUsuario(String login, String filtro, String asignado, Pageable pageable) {

        // 1. Especificación base (filtro de nombre y estado activo)
        Specification<RolesModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }
            p = cb.and(p, cb.equal(root.get("estado"), 1)); // Solo roles activos

            // 2. Subquery para filtrar por asignación ("SI" o "NO")
            if (!"TODOS".equals(asignado)) {
                // SELECT r.codr FROM roles r JOIN usurol ur ON r.codr = ur.codr WHERE ur.login = ?
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<UsuariosModel> usuRoot = subquery.from(UsuariosModel.class);
                Join<UsuariosModel, RolesModel> usuRoles = usuRoot.join("roles");
                subquery.select(usuRoles.get("codr")).where(cb.equal(usuRoot.get("login"), login));

                if ("SI".equals(asignado)) {
                    p = cb.and(p, root.get("codr").in(subquery));
                } else if ("NO".equals(asignado)) {
                    p = cb.and(p, root.get("codr").in(subquery).not());
                }
            }
            return p;
        };

        // 3. Ejecutar la consulta
        Page<RolesModel> pagina = rolRepo.findAll(spec, pageable);

        // 4. Marcar los flags "asignado"
        UsuariosModel usuario = usuRepo.findById(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<Integer> idsAsignados = usuario.getRoles().stream()
                .map(RolesModel::getCodr)
                .collect(Collectors.toSet());

        pagina.getContent().forEach(r -> r.setAsignado(idsAsignados.contains(r.getCodr())));

        return pagina;
    }
    /**
     * B-5. Lista, filtra y pagina los roles.
     */
    public Page<RolesModel> listarPaginado(String filtro, String estado, Pageable pageable) {

        Specification<RolesModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            // Si es "TODOS", no se añade filtro de estado.
            return p;
        };
        return rolRepo.findAll(spec, pageable);
    }

    /**
     * B-5.1. Adicionar Nuevo Rol
     */
    public RolesModel crearRol(RolesModel rol) {
        if (rolRepo.existsByNombreIgnoreCase(rol.getNombre())) {
            throw new DuplicateResourceException("Ya existe un rol con el nombre '" + rol.getNombre() + "'.");
        }
        rol.setEstado(1); // Siempre activo al crear
        return rolRepo.save(rol);
    }

    /**
     * B-5.2. Modificar Datos del Rol
     */
    public RolesModel modificarRol(int codr, RolesModel rolDatos) {
        RolesModel rol = rolRepo.findById(codr)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        if (rolRepo.existsByNombreIgnoreCaseAndCodrNot(rolDatos.getNombre(), codr)) {
            throw new DuplicateResourceException("Ya existe un rol con el nombre '" + rolDatos.getNombre() + "'.");
        }

        rol.setNombre(rolDatos.getNombre());
        return rolRepo.save(rol);
    }

    /**
     * B-5.3. Eliminar (Baja lógica)
     */
    public void eliminarLogico(int codr) {
        RolesModel rol = rolRepo.findById(codr)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        rol.setEstado(0);
        rolRepo.save(rol);
    }

    /**
     * B-5.4. Habilitar Rol
     */
    public void habilitarRol(int codr) {
        RolesModel rol = rolRepo.findById(codr)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        rol.setEstado(1);
        rolRepo.save(rol);
    }

    // (Helper) Buscar por ID
    public RolesModel buscarPorId(int codr) {
        return rolRepo.findById(codr)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
    }
}