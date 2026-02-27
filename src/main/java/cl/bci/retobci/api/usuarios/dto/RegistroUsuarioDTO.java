package cl.bci.retobci.api.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroUsuarioDTO {
    private String name;
    private String email;
    private String password;
    private List<TelefonoDTO> phones;
}
