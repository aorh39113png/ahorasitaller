package com.Proyecto.backEnd.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.Proyecto.backEnd.model.PersonalModel;
import com.Proyecto.backEnd.service.PersonalService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    // ✅ B-3.1 Listar Personal
    @GetMapping
    public ResponseEntity<List<PersonalModel>> listarTodo() {
        List<PersonalModel> lista = personalService.listarTodo();
        // Marcar quién tiene usuario
        lista.forEach(p -> {
            boolean tieneUsuario = personalService.tieneUsuario(p.getCodp());
            p.setTieneClave(tieneUsuario ? 1 : 0);
        });
        return ResponseEntity.ok(lista);
    }

    // ✅ B-3.2 Registrar nuevo Personal
    @PostMapping
    public ResponseEntity<PersonalModel> crear(
            @RequestPart("personal") PersonalModel personal,
            @RequestPart(value = "foto", required = false) MultipartFile foto) throws IOException {
        System.out.println("📩 Recibido objeto personal: " + personal);
        System.out.println("➡️ Nombre: " + personal.getNombre());
        System.out.println("➡️ Apellido: " + personal.getAp());
        System.out.println("➡️ codp (debe ser null): " + personal.getCodp());

        if (foto != null) {
            System.out.println("📷 Foto recibida: " + foto.getOriginalFilename());
        } else {
            System.out.println("⚠️ No llegó foto (foto es null)");
        }

        PersonalModel nuevo = personalService.crear(personal, foto);
        System.out.println("💾 Persona guardada con codp: " + nuevo.getCodp());
        System.out.println("💾 Foto registrada en BD: " + nuevo.getFoto());

        return ResponseEntity.ok(nuevo);
    }

    // ✅ B-3.3 Modificar datos personales
    @PutMapping("/{codp}")
    public ResponseEntity<PersonalModel> modificar(
            @PathVariable int codp,
            @RequestPart("personal") PersonalModel datos,
            @RequestPart(value = "foto", required = false) MultipartFile nuevaFoto) throws IOException {
        PersonalModel actualizado = personalService.modificar(codp, datos, nuevaFoto);
        return ResponseEntity.ok(actualizado);
    }

    // ✅ B-3.4 Eliminar (baja lógica)
    @DeleteMapping("/{codp}")
    public ResponseEntity<Void> eliminar(@PathVariable int codp) {
        personalService.eliminarLogico(codp);
        return ResponseEntity.noContent().build();
    }

    // ✅ B-3.5 Habilitar persona
    @PutMapping("/{codp}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable int codp) {
        personalService.habilitar(codp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profesores-activos")
    public ResponseEntity<List<PersonalModel>> getProfesoresActivos() {
        return ResponseEntity.ok(personalService.listarProfesoresActivos());
    }

    @GetMapping("/estudiantes-activos")
    public ResponseEntity<List<PersonalModel>> getEstudiantesActivos() {
        return ResponseEntity.ok(personalService.listarEstudiantesActivos());
    }
}
