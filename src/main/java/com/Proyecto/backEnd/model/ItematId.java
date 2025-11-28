package com.Proyecto.backEnd.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable 
public class ItematId implements Serializable {

    private String codmat;
    private Integer codi;
    private int gestion;

    public ItematId() {}

    public ItematId(String codmat, Integer codi, int gestion) {
        this.codmat = codmat;
        this.codi = codi;
        this.gestion = gestion;
    }
    
    // Getters, Setters, hashCode, equals
    public String getCodmat() { return codmat; }
    public void setCodmat(String codmat) { this.codmat = codmat; }
    public Integer getCodi() { return codi; }
    public void setCodi(Integer codi) { this.codi = codi; }
    public int getGestion() { return gestion; }
    public void setGestion(int gestion) { this.gestion = gestion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItematId that = (ItematId) o;
        return gestion == that.gestion &&
               Objects.equals(codmat, that.codmat) &&
               Objects.equals(codi, that.codi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codmat, codi, gestion);
    }
}