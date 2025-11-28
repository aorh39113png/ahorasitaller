package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.Proyecto.backEnd.model.GeneralModel;

public interface GeneralRepo extends JpaRepository<GeneralModel, String> {
    
    // Asumimos que solo hay una fila, o que queremos la gestión más alta.
    @Query("SELECT g.gestion FROM GeneralModel g ORDER BY g.gestion DESC LIMIT 1")
    Integer findCurrentGestion();
}