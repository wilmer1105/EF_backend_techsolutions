package com.techsolutions.incidencias.controller;

import com.techsolutions.incidencias.model.*;
import com.techsolutions.incidencias.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@CrossOrigin(origins = "http://localhost:4200")
public class IncidenciaController {

    @Autowired
    private IncidenciaRepository incidenciaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private PrioridadRepository prioridadRepository;
    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private AsignacionRepository asignacionRepository;
    // 1. Añade estos dos endpoints en la parte superior/media de tu controlador:

    @GetMapping("/tecnico/{idTecnico}/asignaciones")
    public List<Asignacion> getAsignacionesPorTecnico(@PathVariable Integer idTecnico) {
        return asignacionRepository.findByTecnicoId(idTecnico);
    }

    @PutMapping("/{idIncidencia}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Integer idIncidencia, @RequestBody EstadoUpdateDTO dto) {
        Incidencia incidencia = incidenciaRepository.findById(idIncidencia).orElse(null);
        Estado nuevoEstado = estadoRepository.findById(dto.getId_estado()).orElse(null);

        if (incidencia != null && nuevoEstado != null) {
            incidencia.setEstado(nuevoEstado);
            incidenciaRepository.save(incidencia);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Estado actualizado correctamente"));
        }
        return ResponseEntity.status(400).body(java.util.Map.of("mensaje", "Error al actualizar estado"));
    }

    // 2. Ve al final de tu IncidenciaController (debajo del AsignacionDTO anterior)
    // y añade este nuevo DTO estático:

    public static class EstadoUpdateDTO {
        private Integer id_estado;

        public EstadoUpdateDTO() {
        }

        public Integer getId_estado() {
            return id_estado;
        }

        public void setId_estado(Integer id_estado) {
            this.id_estado = id_estado;
        }
    }

    // 1. Obtener incidencias por usuario
    @GetMapping("/usuario/{id}")
    public List<Incidencia> getByUsuario(@PathVariable Integer id) {
        return incidenciaRepository.findByUsuario_IdUsuario(id);
    }

    // 2. Registrar nueva incidencia
    @PostMapping("/crear")
    public Incidencia crearIncidencia(@RequestBody IncidenciaRequest request) {
        Incidencia inc = new Incidencia();
        inc.setTitulo(request.getTitulo());
        inc.setDescripcion(request.getDescripcion());

        inc.setUsuario(usuarioRepository.findById(request.getId_usuario()).orElse(null));
        inc.setCategoria(categoriaRepository.findById(request.getId_categoria()).orElse(null));
        inc.setPrioridad(prioridadRepository.findById(request.getId_prioridad()).orElse(null));

        // Estado por defecto: 1 (Pendiente) según datos semilla
        Estado estadoPendiente = estadoRepository.findById(1).orElse(null);
        inc.setEstado(estadoPendiente);

        return incidenciaRepository.save(inc);
    }

    // 3. (Opcional pero recomendado) Endpoints para llenar los combos del
    // formulario
    @GetMapping("/categorias")
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/prioridades")
    public List<Prioridad> getPrioridades() {
        return prioridadRepository.findAll();
    }

    // ... Mantener los endpoints anteriores del usuario e inyectar los nuevos
    // repositorios:

    // 1. Obtener absolutamente todas las incidencias del sistema (Vista Admin)
    @GetMapping("/todas")
    public List<Incidencia> getTodasLasIncidencias() {
        return incidenciaRepository.findAll();
    }

    // 2. Obtener lista de técnicos activos (id_rol = 2) para el combo de asignación
    @GetMapping("/tecnicos")
    public List<Usuario> getTecnicos() {
        return usuarioRepository.findByRolId(2);
    }
    // Busca esto al fondo de tu IncidenciaController.java y reemplázalo:

    public static class AsignacionDTO { // <--- El 'public static' es la clave aquí
        private Integer id_incidencia;
        private Integer id_tecnico;
        private String observacion;

        // Constructor vacío explícito para que Jackson no tenga problemas al instanciar
        public AsignacionDTO() {
        }

        // Mantén tus Getters y Setters exactamente igual:
        public Integer getId_incidencia() {
            return id_incidencia;
        }

        public void setId_incidencia(Integer id_incidencia) {
            this.id_incidencia = id_incidencia;
        }

        public Integer getId_tecnico() {
            return id_tecnico;
        }

        public void setId_tecnico(Integer id_tecnico) {
            this.id_tecnico = id_tecnico;
        }

        public String getObservacion() {
            return observacion;
        }

        public void setObservacion(String observacion) {
            this.observacion = observacion;
        }
    }

    // 3. Endpoint para guardar la asignación y actualizar el estado a "Asignada"
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarIncidencia(@RequestBody AsignacionDTO dto) {
        Incidencia incidencia = incidenciaRepository.findById(dto.getId_incidencia()).orElse(null);
        Usuario tecnico = usuarioRepository.findById(dto.getId_tecnico()).orElse(null);
        Estado estadoAsignado = estadoRepository.findById(2).orElse(null); // Estado 2 = Asignada[cite: 1]

        if (incidencia != null && tecnico != null && estadoAsignado != null) {
            // Registrar asignación
            Asignacion nuevaAsignacion = new Asignacion();
            nuevaAsignacion.setIncidencia(incidencia);
            nuevaAsignacion.setTecnico(tecnico);
            nuevaAsignacion.setObservacion(dto.getObservacion());
            asignacionRepository.save(nuevaAsignacion);

            // Actualizar la incidencia original
            incidencia.setEstado(estadoAsignado);
            incidenciaRepository.save(incidencia);

            return ResponseEntity.ok(java.util.Map.of("mensaje", "Técnico asignado de forma exitosa"));
        }
        return ResponseEntity.status(400).body(java.util.Map.of("mensaje", "Error en los datos de asignación"));

    }

    // Clase DTO de soporte para recibir el JSON desde Angular

}

// Clase de apoyo para recibir los datos del formulario JSON (Puedes ponerla al
// final del archivo o en un paquete DTO)
class IncidenciaRequest {
    private String titulo;
    private String descripcion;
    private Integer id_usuario;
    private Integer id_categoria;
    private Integer id_prioridad;

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String t) {
        titulo = t;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String d) {
        descripcion = d;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id) {
        id_usuario = id;
    }

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id) {
        id_categoria = id;
    }

    public Integer getId_prioridad() {
        return id_prioridad;
    }

    public void setId_prioridad(Integer id) {
        id_prioridad = id;
    }
}
