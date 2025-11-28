package com.Proyecto.backEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.exception.ResourceNotFoundException;
import com.Proyecto.backEnd.model.PersonalModel;
import com.Proyecto.backEnd.model.UsuariosModel;
import com.Proyecto.backEnd.repository.PersonalRepo;
import com.Proyecto.backEnd.repository.UsuariosRepo;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepo usuariosRepo;

    @Autowired
    private PersonalRepo personalRepo;

    public Page<UsuariosModel> listarUsuariosPaginado(String filtro, Pageable pageable) {

        Specification<UsuariosModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("estado"), 1)); // Solo usuarios activos

            if (filtro != null && !filtro.isEmpty()) {
                String filtroLower = "%" + filtro.toLowerCase() + "%";
                Join<UsuariosModel, PersonalModel> personalJoin = root.join("personal");

                p = cb.and(p, cb.or(

                    cb.like(cb.lower(personalJoin.get("nombre")), filtroLower),
                    cb.like(cb.lower(personalJoin.get("ap")), filtroLower),
                    cb.like(cb.lower(personalJoin.get("am")), filtroLower)
                ));
            }
            return p;
        };
        return usuariosRepo.findAll(spec, pageable);
    }

    public UsuariosModel buscarPorLogin(String login) {
        return usuariosRepo.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + login));
    }

    public UsuariosModel crearUsuario(UsuariosModel usuario) {
        if (usuariosRepo.existsByLoginIgnoreCase(usuario.getLogin())) {
            throw new DuplicateResourceException("Ya existe un usuario con el login '" + usuario.getLogin() + "'.");
        }
        if (usuario.getPersonal() != null) {
            PersonalModel per = personalRepo.findById(usuario.getPersonal().getCodp())
                    .orElseThrow(() -> new ResourceNotFoundException("Personal no encontrado"));
            usuario.setPersonal(per);
        }
        usuario.setEstado(1);
        return usuariosRepo.save(usuario);
    }

    public UsuariosModel modificarUsuario(String login, UsuariosModel datos) {
        UsuariosModel u = buscarPorLogin(login);
        if (datos.getLogin() != null && !datos.getLogin().equalsIgnoreCase(u.getLogin())) {
            if (usuariosRepo.existsByLoginIgnoreCase(datos.getLogin())) {
                throw new DuplicateResourceException("El login '" + datos.getLogin() + "' ya está registrado para otro usuario.");
            }
            u.setLogin(datos.getLogin());
        }
        if (datos.getPassword() != null && !datos.getPassword().isEmpty()) {
            u.setPassword(datos.getPassword());
        }
        if (datos.getEstado() >= 0) {
            u.setEstado(datos.getEstado());
        }
        return usuariosRepo.save(u);
    }

    public void eliminarLogico(String login) {
        UsuariosModel u = buscarPorLogin(login);
        u.setEstado(0);
        usuariosRepo.save(u);
    }

    public void habilitarUsuario(String login) {
        UsuariosModel u = buscarPorLogin(login);
        u.setEstado(1);
        usuariosRepo.save(u);
    }
}