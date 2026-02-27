package cl.bci.retobci.api.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage<T> {
    private String mensaje;
    private T data;

    public static <T> ResponseMessage<T> success(String mensaje, T data) {
        return ResponseMessage.<T>builder()
                .mensaje(mensaje)
                .data(data)
                .build();
    }

    public static <T> ResponseMessage<T> success(String mensaje) {
        return ResponseMessage.<T>builder()
                .mensaje(mensaje)
                .build();
    }

    public static <T> ResponseMessage<T> error(String mensaje) {
        return ResponseMessage.<T>builder()
                .mensaje(mensaje)
                .build();
    }
}

