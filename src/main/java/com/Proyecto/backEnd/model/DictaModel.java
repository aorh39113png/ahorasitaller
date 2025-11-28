package com.Proyecto.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="dicta")
@Getter
@Setter
public class DictaModel {

    @EmbeddedId
    private DictaId id;

    // Asumimos que esta columna existe por la UI (B-16)
    private int estado; 

    // Relación con Usuarios (quién registra)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UsuariosModel usuario;

    // --- Relaciones de la Clave Compuesta ---
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codmat") // Mapea 'codmat' del @EmbeddedId
    @JoinColumn(name = "codmat")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MateriasModel materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codpar") // Mapea 'codpar' del @EmbeddedId
    @JoinColumn(name = "codpar")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ParalelosModel paralelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codp") // Mapea 'codp' del @EmbeddedId
    @JoinColumn(name = "codp")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PersonalModel profesor; // Lo llamamos 'profesor' para claridad
}
