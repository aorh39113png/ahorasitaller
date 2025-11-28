package com.Proyecto.backEnd.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; // 👈 IMPORTAR
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="niveles")
@Getter
@Setter
public class NivelesModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codn;

    @Column(length = 60, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int estado;
    
    // --- ✅ CORRECCIÓN (Rompe el bucle) ---
    @OneToMany(mappedBy = "nivel")
    @JsonIgnore // Ignora esta lista al crear el JSON
    private List<MateriasModel> materias;
}