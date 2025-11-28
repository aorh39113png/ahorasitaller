package com.Proyecto.backEnd.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // ✅ Importar
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="procesos")
@Getter
@Setter
public class ProcesosModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int codp;
    
    String nombre;
    String enlace;
    String ayuda;
    int estado;
    
    @ManyToMany(mappedBy = "procesos")
    @JsonIgnoreProperties("procesos") 
    private List<MenusModel> menus;

    // ✅ CAMPO ADICIONAL: No existe en la BD, se usa solo para la UI de B-7.
    @Transient
    private boolean asignado; 
}