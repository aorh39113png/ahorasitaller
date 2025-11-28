package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Proyecto.backEnd.model.DModalidadModel;

// La clave primaria es String (coddm)
public interface DModalidadRepo
        extends JpaRepository<DModalidadModel, String>, JpaSpecificationExecutor<DModalidadModel> {
}