package com.Proyecto.backEnd.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
// ❌ No importar ManyToMany
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="paralelos")
@Getter
@Setter
public class ParalelosModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codpar; 

    private String nombre;
    private int estado;
    
    // --- ❌ RELACIÓN INCORRECTA ELIMINADA ---
    /*
    @ManyToMany(mappedBy = "paralelos")
    @JsonIgnoreProperties("paralelos") 
    private List<MateriasModel> materias;
    */

    // --- ✅ RELACIÓN CORRECTA (Rompe el bucle) ---
    @OneToMany(mappedBy = "paralelo")
    // Ignora el campo 'paralelo' cuando serialices la lista de 'asignacionesMapa'
    @JsonIgnoreProperties("paralelo") 
    private List<MapaModel> asignacionesMapa;
}