package com.Proyecto.backEnd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="items")
@Getter
@Setter
public class ItemsModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codi;

    @Column(length = 40, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int estado;

    // Relación con ITEMAT (B-12.2)
    // Un Item puede estar en muchas asignaciones de materia
    @OneToMany(mappedBy = "item")
    @JsonIgnoreProperties("item") // Rompe el bucle JSON
    private List<ItematModel> asignacionesItemat;
}