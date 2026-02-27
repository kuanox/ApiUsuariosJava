package cl.bci.retobci.api.usuarios.repository;

import cl.bci.retobci.api.usuarios.model.Phone;
import cl.bci.retobci.api.usuarios.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Override
    Optional<Usuario> findById(Long aLong);
    Optional<Usuario> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = """
    UPDATE users SET name = :name,
                    email = :email,
                    password = :password,
                    isActive = :isActive,
                    phones = :phones,
                    token = :token,
                    created = :created,
                    modified = :modified,
                    lastLogin = :lastLogin
    WHERE id = :userId;
    """, nativeQuery = true)
    void updateUser( @Param("userId") Long userId,
                                  @Param("name") String name,
                                  @Param("email") String email,
                                  @Param("password") String password,
                                  @Param("isActive") boolean isActive,
                                  @Param("phones") List<Phone> phones,
                                  @Param("token") String token,
                                  @Param("created") LocalDateTime created,
                                  @Param("modified") LocalDateTime modified,
                                  @Param("lastLogin") LocalDateTime lastLogin);


}
