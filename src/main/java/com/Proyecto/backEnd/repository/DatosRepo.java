package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Proyecto.backEnd.model.DatosModel;

public interface DatosRepo extends JpaRepository<DatosModel, Integer> {
    boolean existsByCedula(String cedula);

    boolean existsByCedulaAndCodpNot(String cedula, Integer codp);
}