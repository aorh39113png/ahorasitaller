package com.Proyecto.backEnd.service;
import com.Proyecto.backEnd.model.DatosModel;
import com.Proyecto.backEnd.model.PersonalModel;
import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.repository.DatosRepo;
import com.Proyecto.backEnd.repository.PersonalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatosService {

    @Autowired
    private DatosRepo datosRepo;

    @Autowired
    private PersonalRepo personalRepo;

    public DatosModel registrarDato(String cedula, int codp) {
        PersonalModel persona = personalRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con codp: " + codp));
        String cedulaNormalizada = cedula == null ? null : cedula.trim();

        if (cedulaNormalizada == null || cedulaNormalizada.isBlank()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía");
        }

        if (datosRepo.existsByCedulaAndCodpNot(cedulaNormalizada, codp)) {
            throw new DuplicateResourceException("Ya existe una persona registrada con la cédula " + cedulaNormalizada);
        }

        DatosModel d = datosRepo.findById(codp).orElse(new DatosModel());
        d.setCedula(cedulaNormalizada);
        d.setPersonal(persona);
        return datosRepo.save(d);
    }
}
