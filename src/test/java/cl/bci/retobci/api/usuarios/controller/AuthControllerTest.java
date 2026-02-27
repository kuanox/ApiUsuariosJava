package cl.bci.retobci.api.usuarios.controller;

import cl.bci.retobci.api.usuarios.dto.LoginRequest;
import cl.bci.retobci.api.usuarios.dto.RegistroUsuarioDTO;
import cl.bci.retobci.api.usuarios.dto.TelefonoDTO;
import cl.bci.retobci.api.usuarios.model.Usuario;
import cl.bci.retobci.api.usuarios.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private RegistroUsuarioDTO registroDTO;
    private LoginRequest loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        TelefonoDTO telefonoDTO = TelefonoDTO.builder()
                .number("987654321")
                .citycode("56")
                .contrycode("56")
                .build();

        registroDTO = RegistroUsuarioDTO.builder()
                .name("Juan Pérez")
                .email("juan@example.com")
                .password("Password123")
                .phones(List.of(telefonoDTO))
                .build();

        loginRequest = LoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .name("Juan Pérez")
                .email("juan@example.com")
                .password("Password123")
                .isActive(true)
                .token("token123")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .phones(List.of())
                .build();
    }

    @Test
    void testRegistroExitoso() throws Exception {
        // Arrange
        when(usuarioService.register(any(RegistroUsuarioDTO.class))).thenReturn(usuario);
        when(usuarioService.toUsuarioResponseDTO(any())).thenCallRealMethod();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario creado exitosamente"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.data.email").value("juan@example.com"));

        verify(usuarioService, times(1)).register(any(RegistroUsuarioDTO.class));
    }

    @Test
    void testRegistroConEmailDuplicado() throws Exception {
        // Arrange
        when(usuarioService.register(any(RegistroUsuarioDTO.class)))
                .thenThrow(new Exception("El correo ya está registrado"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado"));
    }

    @Test
    void testLoginExitoso() throws Exception {
        // Arrange
        when(usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(usuario);
        when(usuarioService.toUsuarioResponseDTO(any())).thenCallRealMethod();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.data.email").value("juan@example.com"));

        verify(usuarioService, times(1)).login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @Test
    void testLoginConCredencialesIncorrectas() throws Exception {
        // Arrange
        when(usuarioService.login(anyString(), anyString()))
                .thenThrow(new Exception("Usuario o contraseña incorrectos"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
    }
}

