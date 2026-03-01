package cl.bci.retobci.api.usuarios.repository;

import cl.bci.retobci.api.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Override
    Optional<Usuario> findById(Long aLong);
    Optional<Usuario> findByEmail(String email);
}


