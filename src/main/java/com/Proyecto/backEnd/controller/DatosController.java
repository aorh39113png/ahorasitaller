package com.Proyecto.backEnd.controller;

import com.Proyecto.backEnd.model.DatosModel;
import com.Proyecto.backEnd.service.DatosService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datos")
@CrossOrigin(origins = "http://localhost:4200")
public class DatosController {

     @Autowired
    private DatosService datosService;

    @PostMapping
    public ResponseEntity<DatosModel> crear(@RequestParam int codp, @RequestParam String cedula) {
        DatosModel nuevo = datosService.registrarDato(cedula, codp);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping
    public ResponseEntity<DatosModel> actualizar(@RequestParam int codp, @RequestParam String cedula) {
        DatosModel actualizado = datosService.registrarDato(cedula, codp);
        return ResponseEntity.ok(actualizado);
    }
}
