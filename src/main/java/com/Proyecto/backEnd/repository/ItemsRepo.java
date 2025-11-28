package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Proyecto.backEnd.model.ItemsModel;

// Extendemos de JpaSpecificationExecutor para poder usar filtros dinámicos
public interface ItemsRepo extends JpaRepository<ItemsModel, Integer>, JpaSpecificationExecutor<ItemsModel> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndCodiNot(String nombre, Integer codi);
}