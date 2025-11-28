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

import com.Proyecto.backEnd.model.ParalelosModel;
import com.Proyecto.backEnd.service.ParalelosService;


@RestController
@RequestMapping("/api/paralelos") // Ruta base para este controlador
@CrossOrigin(origins = "http://localhost:4200")
public class ParalelosController {

    @Autowired
    ParalelosService paralelosService;

    /**
     * B-10. Listar, filtrar y paginar [cite: 860]
     */
    @GetMapping
    public ResponseEntity<Page<ParalelosModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size // B-10 pide 10 elementos por página [cite: 866]
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paralelosService.listarPaginado(filtro, estado, pageable));
    }
@GetMapping("/activos")
    public ResponseEntity<List<ParalelosModel>> listarActivos() {
        return ResponseEntity.ok(paralelosService.listarTodosActivos());
    }
    /**
     * B-10.1. Adicionar Nuevo Paralelo [cite: 875]
     */
    @PostMapping
    public ResponseEntity<ParalelosModel> crear(@RequestBody ParalelosModel paralelo) {
        return ResponseEntity.ok(paralelosService.crearParalelo(paralelo));
    }

    /**
     * B-10.2. Modificar Datos del Paralelo [cite: 897]
     */
    @PutMapping("/{codpar}")
    public ResponseEntity<ParalelosModel> modificar(@PathVariable int codpar, @RequestBody ParalelosModel paralelo) {
        return ResponseEntity.ok(paralelosService.modificarParalelo(codpar, paralelo));
    }

    /**
     * B-10.3. Eliminar Paralelo (Baja lógica) [cite: 925]
     */
    @DeleteMapping("/{codpar}")
    public ResponseEntity<Void> eliminar(@PathVariable int codpar) {
        paralelosService.eliminarLogico(codpar);
        return ResponseEntity.noContent().build();
    }

    /**
     * B-10.4. Habilitar Paralelo [cite: 938]
     */
    @PutMapping("/{codpar}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable int codpar) {
        paralelosService.habilitar(codpar);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint auxiliar para buscar por ID
     */
    @GetMapping("/{codpar}")
    public ResponseEntity<ParalelosModel> buscarPorId(@PathVariable int codpar) {
        return ResponseEntity.ok(paralelosService.buscarPorId(codpar));
    }
}