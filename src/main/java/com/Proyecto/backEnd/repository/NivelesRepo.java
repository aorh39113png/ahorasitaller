package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Proyecto.backEnd.model.NivelesModel;

// Extiende JpaSpecificationExecutor para los filtros (B-13) [cite: 1113]
public interface NivelesRepo extends JpaRepository<NivelesModel, Integer>, JpaSpecificationExecutor<NivelesModel> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndCodnNot(String nombre, Integer codn);
}