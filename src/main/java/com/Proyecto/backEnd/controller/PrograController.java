package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyecto.backEnd.model.PrograId;
import com.Proyecto.backEnd.model.PrograModel;
import com.Proyecto.backEnd.service.PrograService;

@RestController
@RequestMapping("/api/progra")
@CrossOrigin(origins = "http://localhost:4200")
public class PrograController {

    @Autowired
    private PrograService prograService;

    @GetMapping
    public ResponseEntity<Page<PrograModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(required = false) Integer codn, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(prograService.listarPaginado(filtro, estado, codn, pageable));
    }

    @PostMapping
    public ResponseEntity<?> crear(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp, // codp del Alumno
        @RequestParam int gestion,
        @RequestParam String login
    ) {
        PrograModel nueva = prograService.crearInscripcion(codmat, codpar, codp, gestion, login);
        if (nueva == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta inscripción ya existe.");
        }
        return ResponseEntity.ok(nueva);
    }

    @PutMapping
    public ResponseEntity<?> modificar(
        @RequestParam String oldCodmat,
        @RequestParam int oldCodpar,
        @RequestParam int oldCodp,
        @RequestParam int oldGestion,
        @RequestParam String newCodmat,
        @RequestParam int newCodpar,
        @RequestParam int newCodp,
        @RequestParam String login
    ) {
        try {
            PrograModel act = prograService.modificarInscripcion(oldCodmat, oldCodpar, oldCodp, oldGestion, newCodmat, newCodpar, newCodp, login);
            return ResponseEntity.ok(act);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp,
        @RequestParam int gestion
    ) {
        // Creamos el ID compuesto correctamente
        PrograId pid = new PrograId(codpar, codp, codmat, gestion);
        prograService.eliminarLogico(pid);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/habilitar")
    public ResponseEntity<Void> habilitar(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp,
        @RequestParam int gestion
    ) {
        PrograId pid = new PrograId(codpar, codp, codmat, gestion);
        prograService.habilitar(pid);
        return ResponseEntity.noContent().build();
    }
}