package com.techsolutions.incidencias.controller;

import com.techsolutions.incidencias.model.Rol;
import com.techsolutions.incidencias.model.Usuario;
import com.techsolutions.incidencias.repository.RolRepository;
import com.techsolutions.incidencias.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private static final String DOMINIO_CORPORATIVO = "@techsolutions.com";

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/roles")
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioRequest request) {
        if (request.getNombres() == null || request.getNombres().isBlank()
                || request.getApellidos() == null || request.getApellidos().isBlank()
                || request.getCorreoUsuario() == null || request.getCorreoUsuario().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getIdRol() == null) {
            return ResponseEntity.status(400).body(Map.of("mensaje", "Todos los campos son obligatorios."));
        }

        Rol rol = rolRepository.findById(request.getIdRol()).orElse(null);
        if (rol == null) {
            return ResponseEntity.status(400).body(Map.of("mensaje", "El rol seleccionado no existe."));
        }

        String correoCompleto = request.getCorreoUsuario().trim().toLowerCase() + DOMINIO_CORPORATIVO;

        if (usuarioRepository.findByCorreo(correoCompleto).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("mensaje", "Ya existe un usuario registrado con ese correo."));
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setCorreo(correoCompleto);
        usuario.setPassword(request.getPassword());
        usuario.setTelefono(request.getTelefono());
        usuario.setEstado("1");
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    public static class UsuarioRequest {
        private String nombres;
        private String apellidos;
        private String correoUsuario;
        private String password;
        private String telefono;
        private Integer idRol;

        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        public String getCorreoUsuario() { return correoUsuario; }
        public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public Integer getIdRol() { return idRol; }
        public void setIdRol(Integer idRol) { this.idRol = idRol; }
    }
}
