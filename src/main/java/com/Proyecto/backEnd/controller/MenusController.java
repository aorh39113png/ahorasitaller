package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.Proyecto.backEnd.model.MenusModel;
import com.Proyecto.backEnd.service.MenusService;

// ✅ Imports
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
@RequestMapping("/api/menus") // ✅ Ruta base
@CrossOrigin(origins = "http://localhost:4200")
public class MenusController {
    
    @Autowired
    MenusService menService;
    
    /**
     * ✅ B-6. Listar, Filtrar y Paginar Menús
     *
     * @param page (Nro de página, inicia en 0)
     * [cite_start]@param size (Tamaño de página, B-6 pide 10) [cite: 671]
     */
    @GetMapping("/para-rol/{codr}")
    public ResponseEntity<Page<MenusModel>> getMenusParaRol(
            @PathVariable int codr,
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String asignado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size // [cite: 806]
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenusModel> pagina = menService.getMenusParaRol(codr, filtro, asignado, pageable);
        return ResponseEntity.ok(pagina);
    }
    @GetMapping
    public ResponseEntity<Page<MenusModel>> listarMenusPaginado(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size // B-6 pide 10 elementos [cite: 671]
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenusModel> paginaMenus = menService.listarPaginado(filtro, estado, pageable);
        return ResponseEntity.ok(paginaMenus);
    }

    /**
     * ✅ B-6.1. Adicionar Nuevo Menú
     */
    @PostMapping
    public ResponseEntity<MenusModel> crearMenu(@RequestBody MenusModel menu) {
        MenusModel nuevoMenu = menService.crearMenu(menu);
        return ResponseEntity.ok(nuevoMenu);
    }

    /**
     * ✅ B-6.2. Modificar Datos del Menú
     */
    @PutMapping("/{codm}")
    public ResponseEntity<MenusModel> modificarMenu(@PathVariable int codm, @RequestBody MenusModel menuDatos) {
        MenusModel menuActualizado = menService.modificarMenu(codm, menuDatos);
        return ResponseEntity.ok(menuActualizado);
    }

    /**
     * ✅ B-6.3. Eliminar (Baja lógica)
     */
    @DeleteMapping("/{codm}")
    public ResponseEntity<Void> eliminarMenu(@PathVariable int codm) {
        menService.eliminarLogico(codm);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ B-6.4. Habilitar Menú
     */
    @PutMapping("/{codm}/habilitar")
    public ResponseEntity<Void> habilitarMenu(@PathVariable int codm) {
        menService.habilitarMenu(codm);
        return ResponseEntity.noContent().build();
    }

    // (Endpoint auxiliar para obtener un menú antes de modificar)
    @GetMapping("/{codm}")
    public ResponseEntity<MenusModel> buscarMenuPorId(@PathVariable int codm) {
        MenusModel menu = menService.buscarPorId(codm);
        return ResponseEntity.ok(menu);
    }
}