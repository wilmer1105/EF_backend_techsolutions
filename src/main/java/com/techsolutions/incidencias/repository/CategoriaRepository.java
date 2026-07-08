package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // No necesitas escribir nada aquí adentro. JpaRepository ya tiene el método findAll()
}
