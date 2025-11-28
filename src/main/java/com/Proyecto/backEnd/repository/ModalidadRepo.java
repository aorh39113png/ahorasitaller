package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Proyecto.backEnd.model.ModalidadModel;

public interface ModalidadRepo
                extends JpaRepository<ModalidadModel, Integer>, JpaSpecificationExecutor<ModalidadModel> {
        boolean existsByNombre(String nombre);

        boolean existsByNombreAndCodmNot(String nombre, Integer codm);
}