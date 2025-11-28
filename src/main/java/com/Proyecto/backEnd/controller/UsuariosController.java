package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.backEnd.model.UsuariosModel;
import com.Proyecto.backEnd.service.UsuariosService;

@RestController
@RequestMapping("/api/usuarios") // ✅ Ruta base correcta
@CrossOrigin(origins = { "*" })
public class UsuariosController {

    @Autowired
    UsuariosService usuService;
    
    /**
     * B-9. Endpoint para "Lista de Usuarios" (panel izquierdo).
     * Mapea a GET /api/usuarios
     */
    @GetMapping 
    public ResponseEntity<Page<UsuariosModel>> listaUsuariosPaginada(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UsuariosModel> pagina = usuService.listarUsuariosPaginado(filtro, pageable);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Crear nuevo usuario (B-3.5)
     * Mapea a POST /api/usuarios
     */
    @PostMapping 
    public ResponseEntity<UsuariosModel> crearUsuario(@RequestBody UsuariosModel usuario) {
        UsuariosModel nuevo = usuService.crearUsuario(usuario);
        return ResponseEntity.ok(nuevo);
    }

    /**
     * Modificar datos de acceso (B-3.6)
     * Mapea a PUT /api/usuarios/{login}
     */
    @PutMapping("/{login}") 
    public ResponseEntity<UsuariosModel> modificarUsuario(@PathVariable String login, @RequestBody UsuariosModel usuario) {
        UsuariosModel actualizado = usuService.modificarUsuario(login, usuario);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar (baja lógica)
     * Mapea a DELETE /api/usuarios/{login}
     */
    @DeleteMapping("/{login}") 
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String login) {
        usuService.eliminarLogico(login);
        return ResponseEntity.noContent().build();
    }

    /**
     * Habilitar usuario eliminado
     * Mapea a PUT /api/usuarios/{login}/habilitar
     */
    @PutMapping("/{login}/habilitar") 
    public ResponseEntity<Void> habilitarUsuario(@PathVariable String login) {
        usuService.habilitarUsuario(login);
        return ResponseEntity.noContent().build();
    }
}