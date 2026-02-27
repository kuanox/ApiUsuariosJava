package cl.bci.retobci.api.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Pattern(regexp = "^(?:\\$2[aby]\\$.+|(?=.*[a-zA-Z])(?=.*[0-9]).{6,})$", message = "Contraseña debe ser o un hash BCrypt válido, o texto plano con al menos 6 caracteres, incluyendo letras y números")
    private String password;

    private List<TelefonoDTO> phones;

}
