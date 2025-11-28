package com.Proyecto.backEnd.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PrograId implements Serializable {

    private Integer codpar;
    private Integer codp;   // Código del ALUMNO
    private String codmat;
    private int gestion;

    // --- Constructor Vacío (OBLIGATORIO para JPA) ---
    public PrograId() {}

    // --- ✅ Constructor con Parámetros (EL QUE TE FALTABA) ---
    public PrograId(Integer codpar, Integer codp, String codmat, int gestion) {
        this.codpar = codpar;
        this.codp = codp;
        this.codmat = codmat;
        this.gestion = gestion;
    }

    // Getters y Setters
    public Integer getCodpar() { return codpar; }
    public void setCodpar(Integer codpar) { this.codpar = codpar; }
    public Integer getCodp() { return codp; }
    public void setCodp(Integer codp) { this.codp = codp; }
    public String getCodmat() { return codmat; }
    public void setCodmat(String codmat) { this.codmat = codmat; }
    public int getGestion() { return gestion; }
    public void setGestion(int gestion) { this.gestion = gestion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrograId that = (PrograId) o;
        return gestion == that.gestion &&
               Objects.equals(codpar, that.codpar) &&
               Objects.equals(codp, that.codp) &&
               Objects.equals(codmat, that.codmat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codpar, codp, codmat, gestion);
    }
}