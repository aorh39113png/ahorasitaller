package com.Proyecto.backEnd.repository;

import com.Proyecto.backEnd.model.MapaId;
import com.Proyecto.backEnd.model.MapaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MapaRepo extends JpaRepository<MapaModel, MapaId> {
    // Busca todas las asignaciones de una materia en una gestión específica
    List<MapaModel> findById_CodmatAndId_Gestion(String codmat, int gestion);
    
    // ✅ AÑADE ESTE MÉTODO:
    // Busca todas las asignaciones activas de una gestión
    List<MapaModel> findById_GestionAndEstado(int gestion, int estado);
}