package cl.bci.retobci.api.usuarios.dto;

import cl.bci.retobci.api.usuarios.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneRequest {
    private Long id;
    private String number;
    private String citycode;
    private String contrycode;
}
