package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

    // Usamos nativeQuery = true para leer directo de las columnas de la base de datos
    @Query(value = "SELECT * FROM usuarios WHERE id_rol = :idRol", nativeQuery = true)
    List<Usuario> findByRolId(@Param("idRol") Integer idRol);
}