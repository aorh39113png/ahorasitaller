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

import com.Proyecto.backEnd.model.MateriasModel;
import com.Proyecto.backEnd.service.MateriasService;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "http://localhost:4200")
public class MateriasController {

    @Autowired
    MateriasService materiasService;

    /**
     * B-12. Listar, filtrar y paginar
     */
    @GetMapping
    public ResponseEntity<Page<MateriasModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size // B-12 pide 10 [cite: 970]
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(materiasService.listarPaginado(filtro, estado, pageable));
    }
    
    /**
     * B-12.3. Adicionar Nueva Materia
     */
    @PostMapping
    public ResponseEntity<MateriasModel> crear(@RequestBody MateriasModel materia) {
        // El frontend debe enviar: 
        // { "codmat": "SIS-101", "nombre": "Intro", "nivel": { "codn": 1 } }
        return ResponseEntity.ok(materiasService.crearMateria(materia));
    }

    /**
     * B-12.4. Modificar Datos de Materia
     */
    @PutMapping("/{codmat}")
    public ResponseEntity<MateriasModel> modificar(@PathVariable String codmat, @RequestBody MateriasModel materia) {
        return ResponseEntity.ok(materiasService.modificarMateria(codmat, materia));
    }

    /**
     * B-12.5. Eliminar Materia (Baja lógica)
     */
    @DeleteMapping("/{codmat}")
    public ResponseEntity<Void> eliminar(@PathVariable String codmat) {
        materiasService.eliminarLogico(codmat);
        return ResponseEntity.noContent().build();
    }

    /**
     * B-12.6. Habilitar Materia
     */
    @PutMapping("/{codmat}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable String codmat) {
        materiasService.habilitar(codmat);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint auxiliar para buscar por ID
     */
    @GetMapping("/{codmat}")
    public ResponseEntity<MateriasModel> buscarPorId(@PathVariable String codmat) {
        return ResponseEntity.ok(materiasService.buscarPorId(codmat));
    }
}