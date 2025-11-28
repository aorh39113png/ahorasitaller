package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyecto.backEnd.model.DictaId;
import com.Proyecto.backEnd.model.DictaModel;
import com.Proyecto.backEnd.service.DictaService;

@RestController
@RequestMapping("/api/dicta")
@CrossOrigin(origins = "http://localhost:4200")
public class DictaController {

    @Autowired
    private DictaService dictaService;

    /**
     * B-16. Listar, filtrar y paginar
     */
    @GetMapping
    public ResponseEntity<Page<DictaModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(required = false) Integer codn, // Filtro de Nivel
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dictaService.listarPaginado(filtro, estado, codn, pageable));
    }

    /**
     * B-16.1. Adicionar Nueva Asignación
     */
    @PostMapping
    public ResponseEntity<DictaModel> crear(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp,
        @RequestParam int gestion,
        @RequestParam String login // Asumimos que el frontend envía el login del usuario actual
    ) {
        DictaModel nueva = dictaService.crearAsignacion(codmat, codpar, codp, gestion, login);
        return ResponseEntity.ok(nueva);
    }

    /**
     * B-16.3. Eliminar Asignación (Baja lógica)
     */
    @DeleteMapping
    public ResponseEntity<Void> eliminar(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp,
        @RequestParam int gestion
    ) {
        DictaId id = new DictaId(codpar, codp, codmat, gestion);
        dictaService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Habilitar Asignación (Botón 'H' que falta en la UI)
     */
    @PutMapping("/habilitar")
    public ResponseEntity<Void> habilitar(
        @RequestParam String codmat,
        @RequestParam int codpar,
        @RequestParam int codp,
        @RequestParam int gestion
    ) {
        DictaId id = new DictaId(codpar, codp, codmat, gestion);
        dictaService.habilitar(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping
    public ResponseEntity<DictaModel> modificar(
        // --- Clave Vieja (para buscar y borrar)
        @RequestParam String oldCodmat,
        @RequestParam int oldCodpar,
        @RequestParam int oldCodp,
        @RequestParam int oldGestion,
        // --- Clave Nueva (para guardar)
        @RequestParam String newCodmat,
        @RequestParam int newCodpar,
        @RequestParam int newCodp,
        @RequestParam String login
    ) {
        DictaModel actualizada = dictaService.modificarAsignacion(
            oldCodmat, oldCodpar, oldCodp, oldGestion,
            newCodmat, newCodpar, newCodp, login
        );
        return ResponseEntity.ok(actualizada);
    }
}