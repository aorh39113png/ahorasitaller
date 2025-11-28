package com.Proyecto.backEnd.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.exception.DuplicateResourceException;
import com.Proyecto.backEnd.exception.ResourceNotFoundException;
import com.Proyecto.backEnd.model.ItemsModel;
import com.Proyecto.backEnd.repository.ItemsRepo;
import jakarta.persistence.criteria.Predicate;

@Service
public class ItemsService {

    @Autowired
    ItemsRepo itemsRepo;

    /**
     * B-11. Listar, filtrar y paginar
     */
    public Page<ItemsModel> listarPaginado(String filtro, String estado, Pageable pageable) {
        Specification<ItemsModel> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            // Filtro por nombre
            if (filtro != null && !filtro.isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("nombre")), "%" + filtro.toLowerCase() + "%"));
            }

            // Filtro por estado
            if (estado.equals("ACTIVOS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 1));
            } else if (estado.equals("BAJAS")) {
                p = cb.and(p, cb.equal(root.get("estado"), 0));
            }
            // "TODOS" no aplica filtro

            return p;
        };
        return itemsRepo.findAll(spec, pageable);
    }

    /**
     * B-11.1. Adicionar Nuevo Item
     */
    public ItemsModel crear(ItemsModel item) {
        if (itemsRepo.existsByNombreIgnoreCase(item.getNombre())) {
            throw new DuplicateResourceException("Ya existe un item con el nombre '" + item.getNombre() + "'.");
        }
        item.setEstado(1); // Se crea como Activo por defecto
        return itemsRepo.save(item);
    }

    /**
     * B-11.2. Modificar Datos del Item
     */
    public ItemsModel modificar(int codi, ItemsModel datos) {
        ItemsModel i = itemsRepo.findById(codi)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + codi));

        if (itemsRepo.existsByNombreIgnoreCaseAndCodiNot(datos.getNombre(), codi)) {
            throw new DuplicateResourceException("Ya existe un item con el nombre '" + datos.getNombre() + "'.");
        }

        i.setNombre(datos.getNombre());
        return itemsRepo.save(i);
    }

    /**
     * B-11.3. Eliminar Item (Baja lógica)
     */
    public void eliminar(int codi) {
        ItemsModel i = itemsRepo.findById(codi)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + codi));
        i.setEstado(0); // Damos de baja
        itemsRepo.save(i);
    }

    /**
     * B-11.4. Habilitar Item
     */
    public void habilitar(int codi) {
        ItemsModel i = itemsRepo.findById(codi)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + codi));
        i.setEstado(1); // Reactivamos
        itemsRepo.save(i);
    }

    /**
     * Método auxiliar para buscar por ID
     */
    public ItemsModel buscarPorId(int codi) {
         return itemsRepo.findById(codi)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + codi));
    }

    /**
     * B-12.2. Listar solo activos (para el dropdown de Materias)
     */
    public List<ItemsModel> listarActivos() {
        Specification<ItemsModel> spec = (root, query, cb) ->
            cb.equal(root.get("estado"), 1);
        return itemsRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"));
    }
}