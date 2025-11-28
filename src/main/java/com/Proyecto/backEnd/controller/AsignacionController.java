package com.Proyecto.backEnd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.backEnd.model.MapaModel;
import com.Proyecto.backEnd.service.AsignacionService;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    /**
     * ✅ B-7. Endpoint para asignar (Check)
     */
@PostMapping("/usurol")
    public ResponseEntity<Void> asignarUsuarioRol(@RequestParam String login, @RequestParam int codr) {
        asignacionService.asignarUsuarioRol(login, codr);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ B-9. Endpoint para desasignar (Uncheck)
     */
    @DeleteMapping("/usurol")
    public ResponseEntity<Void> desasignarUsuarioRol(@RequestParam String login, @RequestParam int codr) {
        asignacionService.desasignarUsuarioRol(login, codr);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mepro")
    public ResponseEntity<Void> asignar(@RequestParam int codm, @RequestParam int codp) {
        asignacionService.asignarMenuProceso(codm, codp);
        return ResponseEntity.ok().build();
    }
@GetMapping("/mapa/{codmat}")
    public ResponseEntity<List<MapaModel>> getParalelosDeMateria(
            @PathVariable String codmat,
            @RequestParam int gestion) {
        return ResponseEntity.ok(asignacionService.getParalelosDeMateria(codmat, gestion));
    }

    /**
     * B-12.1. Asigna un paralelo a una materia [cite: 1018]
     */
    @PostMapping("/mapa")
    public ResponseEntity<MapaModel> asignarMateriaParalelo(
            @RequestParam String codmat,
            @RequestParam int codpar,
            @RequestParam int gestion) {
        return ResponseEntity.ok(asignacionService.asignarMateriaParalelo(codmat, codpar, gestion));
    }

    /**
     * B-12.1. Elimina la asignación de un paralelo 
     */
    @DeleteMapping("/mapa")
    public ResponseEntity<Void> desasignarMateriaParalelo(
            @RequestParam String codmat,
            @RequestParam int codpar,
            @RequestParam int gestion) {
        asignacionService.desasignarMateriaParalelo(codmat, codpar, gestion);
        return ResponseEntity.noContent().build();
    }
    /**
     * ✅ B-7. Endpoint para desasignar (Uncheck)
     */
    @DeleteMapping("/mepro")
    public ResponseEntity<Void> desasignar(@RequestParam int codm, @RequestParam int codp) {
        asignacionService.desasignarMenuProceso(codm, codp);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/rolme")
    public ResponseEntity<Void> asignarRolMenu(@RequestParam int codr, @RequestParam int codm) {
        asignacionService.asignarRolMenu(codr, codm);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ B-8. Endpoint para desasignar (Uncheck)
     */
    @DeleteMapping("/rolme")
    public ResponseEntity<Void> desasignarRolMenu(@RequestParam int codr, @RequestParam int codm) {
        asignacionService.desasignarRolMenu(codr, codm);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/mapa-activos")
    public ResponseEntity<List<MapaModel>> getMapasActivos(@RequestParam int gestion) {
        return ResponseEntity.ok(asignacionService.getMapasActivos(gestion));
    }
}