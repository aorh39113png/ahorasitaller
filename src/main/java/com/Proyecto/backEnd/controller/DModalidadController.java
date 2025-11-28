package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyecto.backEnd.model.DModalidadModel;
import com.Proyecto.backEnd.service.DModalidadService;

@RestController
@RequestMapping("/api/dmodalidad")
@CrossOrigin(origins = "http://localhost:4200")
public class DModalidadController {

    @Autowired
    DModalidadService dModalidadService;

    @GetMapping
    public ResponseEntity<Page<DModalidadModel>> listar(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dModalidadService.listarPaginado(filtro, estado, pageable));
    }

    @PostMapping
    public ResponseEntity<DModalidadModel> crear(@RequestBody DModalidadModel dm) {
        return ResponseEntity.ok(dModalidadService.crear(dm));
    }

    @PutMapping("/{coddm}")
    public ResponseEntity<DModalidadModel> modificar(@PathVariable String coddm, @RequestBody DModalidadModel dm) {
        return ResponseEntity.ok(dModalidadService.modificar(coddm, dm));
    }

    @DeleteMapping("/{coddm}")
    public ResponseEntity<Void> eliminar(@PathVariable String coddm) {
        dModalidadService.eliminar(coddm);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{coddm}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable String coddm) {
        dModalidadService.habilitar(coddm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{coddm}")
    public ResponseEntity<DModalidadModel> buscarPorId(@PathVariable String coddm) {
        return ResponseEntity.ok(dModalidadService.buscarPorId(coddm));
    }
}