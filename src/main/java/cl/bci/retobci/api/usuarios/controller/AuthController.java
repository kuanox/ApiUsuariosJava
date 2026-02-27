package cl.bci.retobci.api.usuarios.controller;


import cl.bci.retobci.api.usuarios.dto.AuthResponse;
import cl.bci.retobci.api.usuarios.dto.LoginRequest;
import cl.bci.retobci.api.usuarios.dto.RegistroUsuarioDTO;
import cl.bci.retobci.api.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    public ResponseEntity<AuthResponse> register(@RequestBody RegistroUsuarioDTO registroUsuarioDTO) {
        try {
            var user = usuarioService.register(registroUsuarioDTO);
            // Convertir Usuario a UsuarioResponseDTO sin incluir el token
            var usuarioResponseDTO = usuarioService.toUsuarioResponseDTO(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                    .mensaje("Usuario creado exitosamente")
                    .token(user.getToken())
                    .data(usuarioResponseDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthResponse.builder()
                    .mensaje(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            var user = usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword());
            // Convertir Usuario a UsuarioResponseDTO sin incluir el token
            var usuarioResponseDTO = usuarioService.toUsuarioResponseDTO(user);
            return ResponseEntity.ok(AuthResponse.builder()
                    .mensaje("Login exitoso")
                    .token(user.getToken())
                    .data(usuarioResponseDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                    .mensaje(e.getMessage())
                    .build());
        }
    }

}
