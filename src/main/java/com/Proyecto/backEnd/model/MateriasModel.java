package com.Proyecto.backEnd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="materias")
@Getter
@Setter
public class MateriasModel {

    @Id
    @Column(length = 15)
    private String codmat; 

    @Column(length = 30, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int estado;

    // Relación con NIVELES (B-13)
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para que el frontend pueda leer m.nivel.nombre
    @JoinColumn(name = "codn", nullable = false)
    // Rompe el bucle: Materia -> Nivel -> Materia
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "materias"}) 
    private NivelesModel nivel; 

    // --- ❌ RELACIÓN INCORRECTA ELIMINADA ---
    /*
    @ManyToMany
    @JoinTable(
        name = "mapa", 
        joinColumns = @JoinColumn(name = "codmat"),
        inverseJoinColumns = @JoinColumn(name = "codpar")
    )
    @JsonIgnoreProperties("materias")
    private List<ParalelosModel> paralelos;
    */
    
    // --- ✅ RELACIÓN CORRECTA (Rompe el bucle) ---
    @OneToMany(mappedBy = "materia")
    @JsonIgnoreProperties("materia") 
    private List<MapaModel> asignacionesMapa;

    // Relación con ITEMS (B-12.2) via tabla ITEMAT
    @ManyToMany
    @JoinTable(
        name = "itemat",
        joinColumns = @JoinColumn(name = "codmat"),
        inverseJoinColumns = @JoinColumn(name = "codi")
    )
    @JsonIgnoreProperties("materias")
    private List<ItemsModel> items;
}