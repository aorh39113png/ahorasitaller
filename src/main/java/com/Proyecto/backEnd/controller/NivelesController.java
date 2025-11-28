package com.Proyecto.backEnd.controller;

import java.util.List;
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

import com.Proyecto.backEnd.model.NivelesModel;
import com.Proyecto.backEnd.service.NivelesService;

@RestController
@RequestMapping("/api/niveles")
@CrossOrigin(origins = "http://localhost:4200")
public class NivelesController {

    @Autowired
    NivelesService nivelesService;

    /**
     * B-13. Listar, filtrar y paginar [cite: 1094]
     */
    @GetMapping
    public ResponseEntity<Page<NivelesModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size // B-13 pide 10 elementos por página [cite: 1115]
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(nivelesService.listarPaginado(filtro, estado, pageable));
    }

    /**
     * Endpoint para el Dropdown de Materias (B-12).
     * Devuelve TODOS los niveles activos (no paginado). [cite: 1032]
     */
    @GetMapping("/activos")
    public ResponseEntity<List<NivelesModel>> listarActivos() {
        return ResponseEntity.ok(nivelesService.listarTodosActivos());
    }

    /**
     * B-13.1. Adicionar Nuevo Nivel [cite: 1131]
     */
    @PostMapping
    public ResponseEntity<NivelesModel> crear(@RequestBody NivelesModel nivel) {
        return ResponseEntity.ok(nivelesService.crearNivel(nivel));
    }

    /**
     * B-13.2. Modificar Datos del Nivel [cite: 1151]
     */
    @PutMapping("/{codn}")
    public ResponseEntity<NivelesModel> modificar(@PathVariable int codn, @RequestBody NivelesModel nivel) {
        return ResponseEntity.ok(nivelesService.modificarNivel(codn, nivel));
    }

    /**
     * B-13.3. Eliminar Nivel (Baja lógica) [cite: 1169]
     */
    @DeleteMapping("/{codn}")
    public ResponseEntity<Void> eliminar(@PathVariable int codn) {
        nivelesService.eliminarLogico(codn);
        return ResponseEntity.noContent().build();
    }

    /**
     * B-13.4. Habilitar Nivel [cite: 1179]
     */
    @PutMapping("/{codn}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable int codn) {
        nivelesService.habilitar(codn);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint auxiliar para buscar por ID
     */
    @GetMapping("/{codn}")
    public ResponseEntity<NivelesModel> buscarPorId(@PathVariable int codn) {
        return ResponseEntity.ok(nivelesService.buscarPorId(codn));
    }
}