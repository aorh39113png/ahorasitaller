package com.Proyecto.backEnd.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyecto.backEnd.model.ModalidadModel;
import com.Proyecto.backEnd.service.ModalidadService;

@RestController
@RequestMapping("/api/modalidad")
@CrossOrigin(origins = "http://localhost:4200")
public class ModalidadController {

    @Autowired
    ModalidadService modalidadService;

    @GetMapping
    public ResponseEntity<Page<ModalidadModel>> listar(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(modalidadService.listarPaginado(filtro, estado, pageable));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ModalidadModel>> listarActivos() {
        return ResponseEntity.ok(modalidadService.listarActivos());
    }

    @PostMapping
    public ResponseEntity<ModalidadModel> crear(@RequestBody ModalidadModel m) {
        return ResponseEntity.ok(modalidadService.crear(m));
    }

    @PutMapping("/{codm}")
    public ResponseEntity<ModalidadModel> modificar(@PathVariable int codm, @RequestBody ModalidadModel m) {
        return ResponseEntity.ok(modalidadService.modificar(codm, m));
    }

    @DeleteMapping("/{codm}")
    public ResponseEntity<Void> eliminar(@PathVariable int codm) {
        modalidadService.eliminar(codm);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{codm}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable int codm) {
        modalidadService.habilitar(codm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codm}")
    public ResponseEntity<ModalidadModel> buscarPorId(@PathVariable int codm) {
        return ResponseEntity.ok(modalidadService.buscarPorId(codm));
    }
}