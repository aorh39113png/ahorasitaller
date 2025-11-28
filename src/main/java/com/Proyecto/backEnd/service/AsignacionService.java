package com.Proyecto.backEnd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.model.MapaId;
import com.Proyecto.backEnd.model.MapaModel;
import com.Proyecto.backEnd.model.MateriasModel;
import com.Proyecto.backEnd.model.MenusModel;
import com.Proyecto.backEnd.model.ParalelosModel;
import com.Proyecto.backEnd.model.ProcesosModel;
import com.Proyecto.backEnd.model.RolesModel;
import com.Proyecto.backEnd.model.UsuariosModel;
import com.Proyecto.backEnd.repository.MapaRepo;
import com.Proyecto.backEnd.repository.MateriasRepo;
import com.Proyecto.backEnd.repository.MenusRepo;
import com.Proyecto.backEnd.repository.ParalelosRepo;
import com.Proyecto.backEnd.repository.ProcesosRepo;
import com.Proyecto.backEnd.repository.RolesRepo;
import com.Proyecto.backEnd.repository.UsuariosRepo;
import jakarta.transaction.Transactional;

@Service
public class AsignacionService {

    @Autowired
    private MenusRepo menRepo;
    @Autowired
    private ProcesosRepo proRepo;
    @Autowired
    private RolesRepo rolRepo;
    @Autowired
    private UsuariosRepo usuRepo;
    @Autowired
    private MapaRepo mapaRepo; //

    @Autowired
    private MateriasRepo materiasRepo; //

    @Autowired
    private ParalelosRepo paralelosRepo; //

    @Transactional
    public List<MapaModel> getParalelosDeMateria(String codmat, int gestion) {
        return mapaRepo.findById_CodmatAndId_Gestion(codmat, gestion);
    }


@Transactional
    public List<MapaModel> getMapasActivos(int gestion) {
        // Usamos el repo para buscar y forzamos la carga EAGER de las relaciones
        List<MapaModel> mapas = mapaRepo.findById_GestionAndEstado(gestion, 1);
        
        // Forzamos la carga de las relaciones LAZY para evitar errores de JSON
        mapas.forEach(mapa -> {
            mapa.getMateria().getNombre(); // Carga Materia
            mapa.getMateria().getNivel().getNombre(); // Carga Nivel
            mapa.getParalelo().getNombre(); // Carga Paralelo
        });
        
        return mapas;
    }

    /**
     * B-12.1. Asigna un Paralelo a una Materia [cite: 1018, 1021]
     */
    @Transactional
    public MapaModel asignarMateriaParalelo(String codmat, int codpar, int gestion) {
        MateriasModel materia = materiasRepo.findById(codmat)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        ParalelosModel paralelo = paralelosRepo.findById(codpar)
            .orElseThrow(() -> new RuntimeException("Paralelo no encontrado"));

        MapaId id = new MapaId(); //
        id.setCodmat(codmat);
        id.setCodpar(codpar);
        id.setGestion(gestion);

        if (mapaRepo.existsById(id)) {
            throw new RuntimeException("Este paralelo ya está asignado a la materia en esta gestión.");
        }

        MapaModel asignacion = new MapaModel();
        asignacion.setId(id);
        asignacion.setMateria(materia);
        asignacion.setParalelo(paralelo);
        asignacion.setEstado(1); // Activo

        return mapaRepo.save(asignacion);
    }

    /**
     * B-12.1. Elimina la asignación de un Paralelo 
     */
    @Transactional
    public void desasignarMateriaParalelo(String codmat, int codpar, int gestion) {
        MapaId id = new MapaId();
        id.setCodmat(codmat);
        id.setCodpar(codpar);
        id.setGestion(gestion);

        if (!mapaRepo.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada.");
        }
        mapaRepo.deleteById(id);
    }
    // --- B-7 (MEPRO) ---
    @Transactional
    public void asignarMenuProceso(int codm, int codp) {
        // --- ✅ AÑADIR ESTA LÓGICA ---
        MenusModel menu = menRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));
        ProcesosModel proceso = proRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        menu.getProcesos().add(proceso);
        menRepo.save(menu);
    }

    @Transactional
    public void desasignarMenuProceso(int codm, int codp) {
        // --- ✅ AÑADIR ESTA LÓGICA ---
        MenusModel menu = menRepo.findById(codm)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));
        ProcesosModel proceso = proRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        menu.getProcesos().remove(proceso);
        menRepo.save(menu);
    }

    // --- B-8 (ROLME) ---
    @Transactional
    public void asignarRolMenu(int codr, int codm) {
        // ... (Tu código estaba bien) ...
        RolesModel rol = rolRepo.findById(codr).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        MenusModel menu = menRepo.findById(codm).orElseThrow(() -> new RuntimeException("Menú no encontrado"));
        rol.getMenus().add(menu);
        rolRepo.save(rol);
    }

    @Transactional
    public void desasignarRolMenu(int codr, int codm) {
        // ... (Tu código estaba bien) ...
        RolesModel rol = rolRepo.findById(codr).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        MenusModel menu = menRepo.findById(codm).orElseThrow(() -> new RuntimeException("Menú no encontrado"));
        rol.getMenus().remove(menu);
        rolRepo.save(rol);
    }

    // --- B-9 (USUROL) ---
    @Transactional
    public void asignarUsuarioRol(String login, int codr) {
        // ... (Tu código estaba bien) ...
        UsuariosModel usuario = usuRepo.findById(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        RolesModel rol = rolRepo.findById(codr).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.getRoles().add(rol);
        usuRepo.save(usuario);
    }

    @Transactional
    public void desasignarUsuarioRol(String login, int codr) {
        // ... (Tu código estaba bien) ...
        UsuariosModel usuario = usuRepo.findById(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        RolesModel rol = rolRepo.findById(codr).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.getRoles().remove(rol);
        usuRepo.save(usuario);
    }
}