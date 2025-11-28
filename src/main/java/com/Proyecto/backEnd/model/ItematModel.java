package com.Proyecto.backEnd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="itemat")
@Getter
@Setter
public class ItematModel {

    @EmbeddedId
    private ItematId id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @MapsId("codmat") 
    @JoinColumn(name = "codmat")
    @JsonIgnoreProperties({"asignacionesMapa", "items", "asignacionesItemat", "hibernateLazyInitializer", "handler"}) 
    private MateriasModel materia;

    @ManyToOne(fetch = FetchType.EAGER) // Eager para cargar el nombre del item fácilmente
    @MapsId("codi") 
    @JoinColumn(name = "codi")
    @JsonIgnoreProperties({"asignacionesItemat", "hibernateLazyInitializer", "handler"})
    private ItemsModel item;

    private int ponderacion; // 0-100
    private int estado; 
}