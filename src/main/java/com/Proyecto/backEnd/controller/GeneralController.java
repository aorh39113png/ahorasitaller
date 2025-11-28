package com.Proyecto.backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Proyecto.backEnd.service.GeneralService;
import java.util.Map; // Para devolver un JSON simple

@RestController
@RequestMapping("/api/general")
@CrossOrigin(origins = "http://localhost:4200")
public class GeneralController {

    @Autowired
    private GeneralService generalService;

    /**
     * B-12.1. Endpoint para obtener la gestión actual 
     */
    @GetMapping("/gestion-actual")
    public ResponseEntity<?> getGestionActual() {
        int gestion = generalService.getGestionActual();
        // Devuelve un JSON simple: { "gestion": 2025 }
        return ResponseEntity.ok(Map.of("gestion", gestion));
    }
}