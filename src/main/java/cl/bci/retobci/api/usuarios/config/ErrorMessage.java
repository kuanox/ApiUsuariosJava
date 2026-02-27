package cl.bci.retobci.api.usuarios.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private String mensaje;

    public ErrorMessage(String key, String message) {
        this.mensaje = message;
    }

}
