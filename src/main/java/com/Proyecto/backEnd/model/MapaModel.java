package com.Proyecto.backEnd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 👈 IMPORTANTE

// (Asumiendo que MapaId.java ya existe en este paquete y es 'public')

@Entity
@Table(name="mapa")
@Getter
@Setter
public class MapaModel {

    @EmbeddedId
    private MapaId id; 

    // --- ✅ INICIO DE LA CORRECCIÓN DEL ERROR ---
    // Añadimos "hibernateLazyInitializer" y "handler" a la lista de propiedades a ignorar.
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @MapsId("codmat") 
    @JoinColumn(name = "codmat")
    @JsonIgnoreProperties({"asignacionesMapa", "hibernateLazyInitializer", "handler"}) 
    private MateriasModel materia;

    @ManyToOne(fetch = FetchType.LAZY) 
    @MapsId("codpar") 
    @JoinColumn(name = "codpar")
    @JsonIgnoreProperties({"asignacionesMapa", "hibernateLazyInitializer", "handler"})
    private ParalelosModel paralelo;
    // --- ✅ FIN DE LA CORRECCIÓN ---

    private int estado; 
}

