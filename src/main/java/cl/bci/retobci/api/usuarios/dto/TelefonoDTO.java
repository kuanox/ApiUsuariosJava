package cl.bci.retobci.api.usuarios.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefonoDTO {
    private Long id;
    private String number;
    private String citycode;
    private String contrycode;
}
