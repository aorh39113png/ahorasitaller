package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ✅ Importar
import com.Proyecto.backEnd.model.RolesModel;

// ✅ Extender JpaSpecificationExecutor
public interface RolesRepo extends JpaRepository<RolesModel,Integer>, JpaSpecificationExecutor<RolesModel> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndCodrNot(String nombre, Integer codr);
}