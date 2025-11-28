package com.Proyecto.backEnd.service;
import com.Proyecto.backEnd.model.PersonalModel;
import com.Proyecto.backEnd.model.DatosModel;
import com.Proyecto.backEnd.repository.DatosRepo;
import com.Proyecto.backEnd.repository.PersonalRepo;
import com.Proyecto.backEnd.repository.UsuariosRepo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class PersonalService {
    @Autowired
    private PersonalRepo personalRepo;
    @Autowired
    private UsuariosRepo usuariosRepo;
    @Autowired
    private DatosRepo datosRepo;
    @Autowired
    private DatosService datosService;
  

    @Value("${upload.dir}")
    private String uploadDir;

    public boolean tieneUsuario(int codp) {
        return usuariosRepo.existsByPersonalCodp(codp);
    }
    // Listar
    public List<PersonalModel> listarTodo() {
        List<PersonalModel> lista = personalRepo.findAll();
        lista.forEach(p -> {
            DatosModel datos = datosRepo.findById(p.getCodp()).orElse(null);
            if (datos != null) {
                p.setCedula(datos.getCedula());
            }
        });
        return lista;
    }


    public PersonalModel crear(PersonalModel personal, MultipartFile foto) throws IOException {
        personal.setEstado(1);
        System.out.println("🧩 Guardando persona: " + personal.getNombre());

        if (foto != null && !foto.isEmpty()) {
            String nombreFoto = guardarFoto(foto);
            personal.setFoto(nombreFoto);
        } else {
            System.out.println("⚠️ Persona sin foto, se usará la imagen por defecto.");
            personal.setFoto("1760322929192_yas1.jpg");
        }
       PersonalModel guardado = personalRepo.save(personal);
        if (personal.getCedula() != null && !personal.getCedula().isBlank()) {
            datosService.registrarDato(personal.getCedula(), guardado.getCodp());
            guardado.setCedula(personal.getCedula());
        }
        System.out.println("💾 Persona guardada con ID: " + guardado.getCodp());
        return guardado;
    }

    // Modificar
    public PersonalModel modificar(int codp, PersonalModel datos, MultipartFile nuevaFoto) throws IOException {
        PersonalModel p = personalRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        p.setNombre(datos.getNombre());
        p.setAp(datos.getAp());
        p.setAm(datos.getAm());
        p.setGenero(datos.getGenero());
        p.setTipo(datos.getTipo());
        p.setDirec(datos.getDirec());
        p.setTelf(datos.getTelf());
        p.setEcivil(datos.getEcivil());
        p.setFnac(datos.getFnac());
        p.setCedula(datos.getCedula());

        // Lógica para foto
        if (datos.getFoto() != null && datos.getFoto().equals("DEFAULT")) {
        // Si el frontend manda "DEFAULT", forzamos la imagen por defecto
            System.out.println("🔄 Reseteando foto a default.");
            p.setFoto("1760322929192_yas1.jpg");
        } else if (nuevaFoto != null && !nuevaFoto.isEmpty()) {
            // Si viene un archivo nuevo, lo guardamos
            String nombreArchivo = guardarFoto(nuevaFoto);
            p.setFoto(nombreArchivo);
        }
        PersonalModel actualizado = personalRepo.save(p);

        if (datos.getCedula() != null && !datos.getCedula().isBlank()) {
            datosService.registrarDato(datos.getCedula(), codp);
            actualizado.setCedula(datos.getCedula());
        }

        return actualizado;
    }

    // Eliminar lógico
    public void eliminarLogico(int codp) {
        PersonalModel p = personalRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        p.setEstado(0);
        personalRepo.save(p);
    }

    public List<PersonalModel> listarProfesoresActivos() {
        Specification<PersonalModel> spec = (root, query, cb) -> {
            Predicate p1 = cb.equal(root.get("estado"), 1); // Activos
            Predicate p2 = cb.equal(root.get("tipo"), "P"); // Tipo 'P' (Profesor)
            return cb.and(p1, p2);
        };
        return personalRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "ap"));
    }

    // Habilitar
    public void habilitar(int codp) {
        PersonalModel p = personalRepo.findById(codp)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        p.setEstado(1);
        personalRepo.save(p);
    }

    private String guardarFoto(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            System.out.println("⚠️ No se recibió ningún archivo para guardar.");
            return null;
        }

        // EVITAR DUPLICAR LA FOTO POR DEFECTO
        // Si el archivo se llama igual que la default, retornamos el nombre fijo y NO
        // guardamos nada.
        if ("1760322929192_yas1.jpg".equals(archivo.getOriginalFilename())) {
            System.out.println("⚠️ Se intentó subir la foto default. Usando la existente.");
            return "1760322929192_yas1.jpg";
        }

        // ✅ Ruta absoluta dentro del backend
        Path directorio = Paths.get("src/uploads/fotos").toAbsolutePath().normalize();

        // Crea carpeta si no existe
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
            System.out.println("📁 Carpeta creada: " + directorio);
        }

        // Nombre único
        String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
        Path destino = directorio.resolve(nombreArchivo);

        // Guarda físicamente
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✅ Archivo guardado: " + nombreArchivo);
        System.out.println("📂 Ruta absoluta: " + destino.toAbsolutePath());

        return nombreArchivo;
    }

    public List<PersonalModel> listarEstudiantesActivos() {
        Specification<PersonalModel> spec = (root, query, cb) -> {
            Predicate p1 = cb.equal(root.get("estado"), 1); // Activos
            // Corregido: 'E' como String
            Predicate p2 = cb.equal(root.get("tipo"), "E"); // Tipo 'E' (Estudiante)
            return cb.and(p1, p2);
        };
        return personalRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "ap"));
    }
}
