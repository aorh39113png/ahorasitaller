package com.Proyecto.backEnd.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DictaId implements Serializable {

    private Integer codpar;
    private Integer codp;
    private String codmat;
    private int gestion;

    // --- Constructores ---
    public DictaId() {}

    public DictaId(Integer codpar, Integer codp, String codmat, int gestion) {
        this.codpar = codpar;
        this.codp = codp;
        this.codmat = codmat;
        this.gestion = gestion;
    }

    // --- Getters y Setters ---
    // (Lombok no siempre funciona bien con @Embeddable, los generamos manualmente)
    public Integer getCodpar() { return codpar; }
    public void setCodpar(Integer codpar) { this.codpar = codpar; }
    public Integer getCodp() { return codp; }
    public void setCodp(Integer codp) { this.codp = codp; }
    public String getCodmat() { return codmat; }
    public void setCodmat(String codmat) { this.codmat = codmat; }
    public int getGestion() { return gestion; }
    public void setGestion(int gestion) { this.gestion = gestion; }

    // --- hashCode() y equals() (Obligatorios) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictaId dictaId = (DictaId) o;
        return gestion == dictaId.gestion &&
               Objects.equals(codpar, dictaId.codpar) &&
               Objects.equals(codp, dictaId.codp) &&
               Objects.equals(codmat, dictaId.codmat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codpar, codp, codmat, gestion);
    }
}
