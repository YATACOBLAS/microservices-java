package org.blas.springcloud.msvc.usuarios.msvcusuarios.repositories;

import org.blas.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email);

}
