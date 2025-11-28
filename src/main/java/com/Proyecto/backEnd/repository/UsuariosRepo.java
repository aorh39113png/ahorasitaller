package com.Proyecto.backEnd.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ✅ Importar
import com.Proyecto.backEnd.model.UsuariosModel;

// ✅ Extender JpaSpecificationExecutor
public interface UsuariosRepo extends JpaRepository<UsuariosModel,String>, JpaSpecificationExecutor<UsuariosModel> {
    Optional<UsuariosModel> findByLogin(String login);
    boolean existsByPersonalCodp(int codp);
    boolean existsByLoginIgnoreCase(String login);
}