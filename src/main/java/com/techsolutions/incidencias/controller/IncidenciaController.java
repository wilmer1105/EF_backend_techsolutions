package com.techsolutions.incidencias.controller;

import com.techsolutions.incidencias.model.*;
import com.techsolutions.incidencias.repository.*;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
    @Autowired
    private HistorialEstadoRepository historialEstadoRepository;
    // 1. Añade estos dos endpoints en la parte superior/media de tu controlador:

    @GetMapping("/tecnico/{idTecnico}/asignaciones")
    public List<Incidencia> getAsignacionesPorTecnico(@PathVariable Integer idTecnico) {
        return incidenciaRepository.findByTecnico_IdUsuario(idTecnico);
    }

    @PutMapping("/{idIncidencia}/estado")
public ResponseEntity<?> actualizarEstado(@PathVariable Integer idIncidencia, @RequestBody EstadoUpdateDTO dto) {
    Incidencia incidencia = incidenciaRepository.findById(idIncidencia).orElse(null);
    Estado nuevoEstado = estadoRepository.findById(dto.getId_estado()).orElse(null);

    if (incidencia != null && nuevoEstado != null) {
        incidencia.setEstado(nuevoEstado);

        String nombreEstado = nuevoEstado.getNombre_estado();
        if ("En Proceso".equalsIgnoreCase(nombreEstado) && incidencia.getFecha_inicio() == null) {
            incidencia.setFecha_inicio(java.time.LocalDateTime.now());
        }
        if (("Resuelta".equalsIgnoreCase(nombreEstado) || "Cerrada".equalsIgnoreCase(nombreEstado))
                && incidencia.getFecha_fin() == null) {
            incidencia.setFecha_fin(java.time.LocalDateTime.now());
        }

        incidenciaRepository.save(incidencia);

        // AUDITORÍA: Guardamos la marca de tiempo exacta de inicio o fin de resolución
        String detalleLog = "El técnico cambió el estado a: " + nombreEstado;
        HistorialEstado historial = new HistorialEstado(
            incidencia, 
            nuevoEstado, 
            incidencia.getTecnico(), 
            detalleLog
        );
        historialEstadoRepository.save(historial);

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Línea de tiempo actualizada"));
    }
    return ResponseEntity.status(400).body(java.util.Map.of("mensaje", "Error al procesar el cambio de estado"));
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
    @PutMapping("/{idIncidencia}/prioridad")
    public ResponseEntity<?> actualizarPrioridad(@PathVariable Integer idIncidencia, @RequestBody PrioridadUpdateDTO dto) {
        Incidencia incidencia = incidenciaRepository.findById(idIncidencia).orElse(null);
        Prioridad nuevaPrioridad = prioridadRepository.findById(dto.getId_prioridad()).orElse(null);

        if (incidencia != null && nuevaPrioridad != null) {
            incidencia.setPrioridad(nuevaPrioridad);
            incidenciaRepository.save(incidencia);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Gravedad de la incidencia actualizada"));
        }
        return ResponseEntity.status(400).body(java.util.Map.of("mensaje", "Error al actualizar la prioridad"));
    }

// Al final de tu IncidenciaController.java, agrega este DTO estático:
    public static class PrioridadUpdateDTO {
        private Integer id_prioridad;
        public PrioridadUpdateDTO() {}
        public Integer getId_prioridad() { return id_prioridad; }
        public void setId_prioridad(Integer id_prioridad) { this.id_prioridad = id_prioridad; }
    }

    // 1. Obtener incidencias por usuario
    @GetMapping("/usuario/{id}")
    public List<Incidencia> getByUsuario(@PathVariable Integer id) {
        return incidenciaRepository.findByUsuario_IdUsuario(id);
    }

    // 2. Registrar nueva incidencia
    // 2. Registrar nueva incidencia con auditoría de tiempos inicial
    @PostMapping("/crear")
    public Incidencia crearIncidencia(@RequestBody IncidenciaRequest request) {
        Incidencia inc = new Incidencia();
        inc.setTitulo(request.getTitulo());
        inc.setDescripcion(request.getDescripcion());

        inc.setUsuario(usuarioRepository.findById(request.getId_usuario()).orElse(null));
        inc.setCategoria(categoriaRepository.findById(request.getId_categoria()).orElse(null));
        inc.setPrioridad(prioridadRepository.findById(request.getId_prioridad()).orElse(null));

        // Setear fecha de registro en el objeto principal
        inc.setFecha_registro(java.time.LocalDateTime.now());

        // Estado por defecto: 1 (Pendiente) según datos semilla
        Estado estadoPendiente = estadoRepository.findById(1).orElse(null);
        inc.setEstado(estadoPendiente);

        // Guardamos la incidencia
        Incidencia incidenciaGuardada = incidenciaRepository.save(inc);

        // AUDITORÍA INICIAL: Guardamos la marca de tiempo en la tabla de historial
        HistorialEstado historialInicial = new HistorialEstado();
        historialInicial.setIncidencia(incidenciaGuardada);
        historialInicial.setEstado(estadoPendiente);
        historialInicial.setTecnico(null); // Al registrarse, aún no hay técnico asignado
        historialInicial.setFecha_cambio(java.time.LocalDateTime.now());
        historialInicial.setObservacion("Incidencia registrada inicialmente por el empleado.");
        
        historialEstadoRepository.save(historialInicial);

        return incidenciaGuardada;
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

    @GetMapping("/estados")
    public List<Estado> getEstados() {
        return estadoRepository.findAll();
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
     // O 'tecnico_asignado', verifica cómo se llama este atributo en tu Java // Representa al técnico asignado

    // Busca esto al fondo de tu IncidenciaController.java y reemplázalo:

    public static class AsignacionDTO { // <--- El 'public static' es la clave aquí
        private Integer id_incidencia;
        private Integer id_tecnico;
        private String observacion;
        private Usuario tecnico;

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
        public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }
    }

    @PostMapping("/asignar")
    public ResponseEntity<?> asignarTecnico(@RequestBody AsignacionDTO dto) {
        // 1. Buscar las entidades en la base de datos
        Incidencia incidencia = incidenciaRepository.findById(dto.getId_incidencia()).orElse(null);
        Usuario tecnico = usuarioRepository.findById(dto.getId_tecnico()).orElse(null);
        Estado estadoAsignado = estadoRepository.findById(2).orElse(null); // ID 2 = Asignada

        // 2. Validar que nada venga nulo
        if (incidencia == null || tecnico == null || estadoAsignado == null) {
            return ResponseEntity.status(400).body(
                    java.util.Map.of("mensaje", "Error: No se encontraron los IDs de incidencia, técnico o estado."));
        }

        // 3. Actualizar la Incidencia principal (Quita el null de tu JSON)
        incidencia.setTecnico(tecnico);
        incidencia.setEstado(estadoAsignado);
        incidencia.setFecha_asignacion(java.time.LocalDateTime.now());
        incidenciaRepository.save(incidencia);

        // 4. Registrar en la tabla clásica de asignaciones (Opcional, si la usas)
        // Asignacion nuevaAsig = new Asignacion();
        // nuevaAsig.setIncidencia(incidencia);
        // nuevaAsig.setTecnico(tecnico);
        // nuevaAsig.setObservacion(dto.getObservacion());
        // nuevaAsig.setFecha_asignacion(java.time.LocalDateTime.now());
        // asignacionRepository.save(nuevaAsig);

        // 5. CORRECCIÓN: Primero instanciamos el objeto 'historial'
        HistorialEstado historial = new HistorialEstado();
        historial.setIncidencia(incidencia);
        historial.setEstado(estadoAsignado);
        historial.setTecnico(tecnico);
        historial.setFecha_cambio(java.time.LocalDateTime.now()); // Marca de tiempo de la asignación
        historial.setObservacion(
                "Incidencia asignada al técnico: " + tecnico.getNombres() + ". Obs: " + dto.getObservacion());

        // 6. Ahora que 'historial' ya existe y está lleno, lo guardamos
        historialEstadoRepository.save(historial);

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Asignación procesada de manera exitosa"));
    }

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
