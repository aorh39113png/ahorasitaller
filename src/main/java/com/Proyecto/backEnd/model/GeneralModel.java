package com.Proyecto.backEnd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="general")
@Getter
@Setter
public class GeneralModel {
    
    @Id
    private String codg; //

    private int gestion; //

    @ManyToOne
    @JoinColumn(name = "login")
    private UsuariosModel usuario; //
}