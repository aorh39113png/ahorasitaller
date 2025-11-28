package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Importar
import com.Proyecto.backEnd.model.MenusModel;

public interface MenusRepo extends JpaRepository<MenusModel,Integer>, JpaSpecificationExecutor<MenusModel> { // Añadir
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndCodmNot(String nombre, Integer codm);
}