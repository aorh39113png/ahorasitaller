package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.Proyecto.backEnd.model.MenusModel;
import com.Proyecto.backEnd.model.ProcesosModel;
import com.Proyecto.backEnd.service.ProcesosService;

// ✅ Imports
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/procesos") // ✅ Ruta base
@CrossOrigin(origins = "http://localhost:4200")
public class ProcesosController {

    @Autowired
    ProcesosService proService;
    
    /**
     * ✅ B-7. Endpoint para la "Lista de Procesos" de la derecha.
     *
     * @param codm   El ID del menú seleccionado a la izquierda.
     * @param filtro   Filtro por nombre de proceso.
     * @param asignado "TODOS", "SI", "NO".
     * @param page     Página (0-based).
     * @param size     Tamaño (B-7 pide 10)[cite: 774].
     */
    @GetMapping("/para-menu/{codm}")
    public ResponseEntity<Page<ProcesosModel>> getProcesosParaMenu(
            @PathVariable int codm,
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String asignado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProcesosModel> pagina = proService.getProcesosParaMenu(codm, filtro, asignado, pageable);
        return ResponseEntity.ok(pagina);
    }

    /**
     * ✅ Endpoint genérico para listar procesos (si se necesitara en otra pantalla).
     */
    @GetMapping
    public ResponseEntity<Page<ProcesosModel>> listaProcesos(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProcesosModel> pagina = proService.listaProcesos(filtro, estado, pageable);
        return ResponseEntity.ok(pagina);
    }
}