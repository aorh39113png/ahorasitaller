package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Proyecto.backEnd.model.MateriasModel;

public interface MateriasRepo extends JpaRepository<MateriasModel, String>, JpaSpecificationExecutor<MateriasModel> {
    // Usamos String como tipo de ID porque codmat (Sigla) es la PK
}