package cl.bci.retobci.api.usuarios.controller;

import cl.bci.retobci.api.usuarios.dto.ResponseMessage;
import cl.bci.retobci.api.usuarios.dto.UsuarioRequest;
import cl.bci.retobci.api.usuarios.dto.UsuarioResponseDTO;
import cl.bci.retobci.api.usuarios.model.Usuario;
import cl.bci.retobci.api.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Validated
@Tag(name = "Users Management", description = "APIs for managing users, including registration and retrieval")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found")
    })
    public ResponseEntity<ResponseMessage<List<UsuarioResponseDTO>>> getAllUsers() {
        try {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();

            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(ResponseMessage.success("No hay usuarios disponibles", null));
            }

            List<UsuarioResponseDTO> response = usuarios.stream()
                    .map(usuarioService::toUsuarioResponseDTO)
                    .toList();

            return ResponseEntity.ok(ResponseMessage.success("Usuarios obtenidos exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessage.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseMessage<UsuarioResponseDTO>> getUserById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.getUserById(id);
            UsuarioResponseDTO response = usuarioService.toUsuarioResponseDTO(usuario);
            return ResponseEntity.ok(ResponseMessage.success("Usuario obtenido exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseMessage.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseMessage<UsuarioResponseDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        try {
            Usuario usuario = usuarioService.updateUser(id, usuarioRequest);
            UsuarioResponseDTO response = usuarioService.toUsuarioResponseDTO(usuario);
            return ResponseEntity.ok(ResponseMessage.success("Usuario actualizado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseMessage.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseMessage<?>> deleteUser(@PathVariable Long id) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok(ResponseMessage.success("Usuario eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseMessage.error(e.getMessage()));
        }
    }


}
