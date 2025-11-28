package com.Proyecto.backEnd.repository;

import com.Proyecto.backEnd.model.PrograId;
import com.Proyecto.backEnd.model.PrograModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrograRepo extends JpaRepository<PrograModel, PrograId>, JpaSpecificationExecutor<PrograModel> {
}