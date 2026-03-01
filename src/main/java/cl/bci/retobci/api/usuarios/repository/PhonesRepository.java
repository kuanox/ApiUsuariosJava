package cl.bci.retobci.api.usuarios.repository;

import cl.bci.retobci.api.usuarios.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhonesRepository extends JpaRepository<Phone, Long> {
}
