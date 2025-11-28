package com.Proyecto.backEnd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dmodalidad")
@Getter
@Setter
public class DModalidadModel {

    @Id
    @Column(length = 15)
    private String coddm; // PK Manual (Texto)

    @Column(length = 40, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int estado;

    // Relación con MODALIDAD (B-14)
    // Usamos EAGER para cargar el nombre de la modalidad padre fácilmente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codm", nullable = false)
    @JsonIgnoreProperties({ "detalles", "hibernateLazyInitializer", "handler" })
    private ModalidadModel modalidad;

    // Manual Getters and Setters to resolve Lombok issues

    public String getCoddm() {
        return coddm;
    }

    public void setCoddm(String coddm) {
        this.coddm = coddm;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public ModalidadModel getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadModel modalidad) {
        this.modalidad = modalidad;
    }
}