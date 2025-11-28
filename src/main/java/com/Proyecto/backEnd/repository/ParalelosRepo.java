package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Proyecto.backEnd.model.ParalelosModel;

// Extiende JpaSpecificationExecutor para usar la API Criteria para filtros
public interface ParalelosRepo extends JpaRepository<ParalelosModel, Integer>, JpaSpecificationExecutor<ParalelosModel> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndCodparNot(String nombre, Integer codpar);
}