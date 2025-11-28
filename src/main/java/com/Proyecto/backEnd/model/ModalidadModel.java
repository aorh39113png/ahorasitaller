package com.Proyecto.backEnd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "modalidad")
@Getter
@Setter
public class ModalidadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codm;

    @Column(length = 40, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int estado; // 1=activo, 0=nulo

    // Relación con DMODALIDAD (B-15)
    @OneToMany(mappedBy = "modalidad")
    @JsonIgnore // Evita bucles infinitos al serializar JSON
    private List<DModalidadModel> detalles;
}