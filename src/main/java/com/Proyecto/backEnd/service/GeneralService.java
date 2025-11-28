package com.Proyecto.backEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Proyecto.backEnd.repository.GeneralRepo;
import java.time.Year; // Asegúrate de que java.time.Year esté importado

@Service
public class GeneralService {

    @Autowired
    private GeneralRepo generalRepo;

    /**
     * B-12.1. Obtiene la gestión actual
     */
    public int getGestionActual() {
        Integer gestion = generalRepo.findCurrentGestion();
        if (gestion == null) {
            // --- INICIO DE LA CORRECCIÓN ---
            // Valor por defecto si la tabla está vacía
            // .getValue() convierte el objeto 'Year' en un 'int'
            return Year.now().getValue(); 
            // --- FIN DE LA CORRECCIÓN ---
        }
        return gestion;
    }
}