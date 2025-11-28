package com.Proyecto.backEnd.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Esta es la clase para la Clave Primaria Compuesta de la tabla MAPA.
 * Es 'public' para ser visible desde el paquete 'repository'.
 */
@Embeddable 
public class MapaId implements Serializable {

    private String codmat;
    private Integer codpar;
    private int gestion;

    // --- Constructores (JPA los necesita) ---
    public MapaId() {}

    public MapaId(String codmat, Integer codpar, int gestion) {
        this.codmat = codmat;
        this.codpar = codpar;
        this.gestion = gestion;
    }
    
    // --- Getters y Setters ---
    public String getCodmat() { return codmat; }
    public void setCodmat(String codmat) { this.codmat = codmat; }
    public Integer getCodpar() { return codpar; }
    public void setCodpar(Integer codpar) { this.codpar = codpar; }
    public int getGestion() { return gestion; }
    public void setGestion(int gestion) { this.gestion = gestion; }

    // --- hashCode() y equals() (OBLIGATORIOS para @EmbeddedId) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapaId mapaId = (MapaId) o;
        return gestion == mapaId.gestion &&
               Objects.equals(codmat, mapaId.codmat) &&
               Objects.equals(codpar, mapaId.codpar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codmat, codpar, gestion);
    }
}

