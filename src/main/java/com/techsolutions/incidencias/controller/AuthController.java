package com.techsolutions.incidencias.controller;

import com.techsolutions.incidencias.model.Usuario;
import com.techsolutions.incidencias.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Permite la conexión con Angular
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String correo = credentials.get("correo");
        String password = credentials.get("password");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // ... dentro de tu método login en AuthController.java
            if (usuario.getPassword().equals(password)) {
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Login exitoso",
                        "id_rol", usuario.getRol().getId_rol(),
                        "id_usuario", usuario.getIdUsuario(), // Usamos el método correcto generado por Lombok
                        "nombres", usuario.getNombres()));
            }
        }
        return ResponseEntity.status(401).body(Map.of("mensaje", "Credenciales inválidas"));
    }
}