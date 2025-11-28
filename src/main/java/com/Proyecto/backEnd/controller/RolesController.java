package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.Proyecto.backEnd.model.RolesModel;
import com.Proyecto.backEnd.service.RolesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/roles") // Ruta base
@CrossOrigin(origins = "http://localhost:4200")
public class RolesController {
    @Autowired
    RolesService rolService;
    
    /**
     * B-5. Listar, Filtrar y Paginar Roles
     */
    @GetMapping
    public ResponseEntity<Page<RolesModel>> listarRolesPaginado(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RolesModel> paginaRoles = rolService.listarPaginado(filtro, estado, pageable);
        return ResponseEntity.ok(paginaRoles);
    }

    // --- ✅ ENDPOINT FALTANTE (B-9) ---
    /**
     * B-9. Endpoint para "Lista de Roles" (panel derecho).
     */
    @GetMapping("/para-usuario/{login}") // La ruta que faltaba
    public ResponseEntity<Page<RolesModel>> getRolesParaUsuario(
            @PathVariable String login,
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String asignado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RolesModel> pagina = rolService.getRolesParaUsuario(login, filtro, asignado, pageable);
        return ResponseEntity.ok(pagina);
    }
    // --- FIN ---

    @PostMapping
    public ResponseEntity<RolesModel> crearRol(@RequestBody RolesModel rol) {
        RolesModel nuevoRol = rolService.crearRol(rol);
        return ResponseEntity.ok(nuevoRol);
    }

    @PutMapping("/{codr}")
    public ResponseEntity<RolesModel> modificarRol(@PathVariable int codr, @RequestBody RolesModel rolDatos) {
        RolesModel rolActualizado = rolService.modificarRol(codr, rolDatos);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{codr}")
    public ResponseEntity<Void> eliminarRol(@PathVariable int codr) {
        rolService.eliminarLogico(codr);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{codr}/habilitar")
    public ResponseEntity<Void> habilitarRol(@PathVariable int codr) {
        rolService.habilitarRol(codr);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codr}")
    public ResponseEntity<RolesModel> buscarRolPorId(@PathVariable int codr) {
        RolesModel rol = rolService.buscarPorId(codr);
        return ResponseEntity.ok(rol);
    }
}