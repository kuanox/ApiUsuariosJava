package cl.bci.retobci.api.usuarios.config;

import cl.bci.retobci.api.usuarios.dto.ResponseMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseMessage.error(mensaje));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage<?>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        String mensaje = "JSON inválido o formato incorrecto: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseMessage.error(mensaje));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseMessage<?>> handleDataAccess(DataAccessException ex) {
        String mensaje = "Error de base de datos: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessage.error(mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<?>> handleGeneric(Exception ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseMessage.error(mensaje));
    }
}
