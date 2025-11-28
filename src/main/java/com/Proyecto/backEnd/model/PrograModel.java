package com.Proyecto.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "progra")
@Getter
@Setter
public class PrograModel {

    @EmbeddedId
    private PrograId id;

    private int estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private UsuariosModel usuario; // Quién inscribe

    // Relaciones de la clave compuesta

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codmat")
    @JoinColumn(name = "codmat")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private MateriasModel materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codpar")
    @JoinColumn(name = "codpar")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private ParalelosModel paralelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codp")
    @JoinColumn(name = "codp")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PersonalModel alumno; // Personal tipo 'E'
}