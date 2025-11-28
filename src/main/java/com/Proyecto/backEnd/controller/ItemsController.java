package com.Proyecto.backEnd.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyecto.backEnd.model.ItemsModel;
import com.Proyecto.backEnd.service.ItemsService;

@RestController
@RequestMapping("/api/items") // Ruta base
@CrossOrigin(origins = "http://localhost:4200")
public class ItemsController {

    @Autowired
    ItemsService itemsService;

    /**
     * B-11. Listar ítems con paginación y filtros
     */
    @GetMapping
    public ResponseEntity<Page<ItemsModel>> listar(
        @RequestParam(required = false, defaultValue = "") String filtro,
        @RequestParam(defaultValue = "TODOS") String estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size // B-11 pide 10 elementos
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(itemsService.listarPaginado(filtro, estado, pageable));
    }

    /**
     * Extra: Listar todos los activos (sin paginar)
     * (Necesario para el dropdown de Materias B-12.2)
     */
    @GetMapping("/activos")
    public ResponseEntity<List<ItemsModel>> listarActivos() {
        return ResponseEntity.ok(itemsService.listarActivos());
    }

    /**
     * B-11.1. Crear nuevo ítem
     */
    @PostMapping
    public ResponseEntity<ItemsModel> crear(@RequestBody ItemsModel item) {
        return ResponseEntity.ok(itemsService.crear(item));
    }

    /**
     * B-11.2. Modificar ítem
     */
    @PutMapping("/{codi}")
    public ResponseEntity<ItemsModel> modificar(@PathVariable int codi, @RequestBody ItemsModel item) {
        return ResponseEntity.ok(itemsService.modificar(codi, item));
    }

    /**
     * B-11.3. Eliminar ítem (Baja lógica)
     */
    @DeleteMapping("/{codi}")
    public ResponseEntity<Void> eliminar(@PathVariable int codi) {
        itemsService.eliminar(codi);
        return ResponseEntity.noContent().build();
    }

    /**
     * B-11.4. Habilitar ítem
     */
    @PutMapping("/{codi}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable int codi) {
        itemsService.habilitar(codi);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtener ítem por ID
     */
    @GetMapping("/{codi}")
    public ResponseEntity<ItemsModel> buscarPorId(@PathVariable int codi) {
        return ResponseEntity.ok(itemsService.buscarPorId(codi));
    }
}