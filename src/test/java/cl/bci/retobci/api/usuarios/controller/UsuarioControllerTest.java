package cl.bci.retobci.api.usuarios.controller;

import cl.bci.retobci.api.usuarios.dto.UsuarioRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequest updateRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .name("Juan Pérez")
                .email("juan@example.com")
                .password("Password123")
                .isActive(true)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .phones(List.of())
                .build();

        updateRequest = UsuarioRequest.builder()
                .name("Juan Carlos")
                .email("juan.carlos@example.com")
                .build();
    }

    @Test
    @WithMockUser(username = "juan@example.com")
    void testGetAllUsuarios() throws Exception {
        // Arrange
        when(usuarioService.getAllUsuarios()).thenReturn(List.of(usuario));
        when(usuarioService.toUsuarioResponseDTO(any())).thenCallRealMethod();

        // Act & Assert
        mockMvc.perform(get("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuarios obtenidos exitosamente"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].email").value("juan@example.com"))
                .andExpect(jsonPath("$.data[0].name").value("Juan Pérez"));

        verify(usuarioService, times(1)).getAllUsuarios();
    }

    @Test
    @WithMockUser(username = "juan@example.com")
    void testGetUsuarioById() throws Exception {
        // Arrange
        when(usuarioService.getUserById(1L)).thenReturn(usuario);
        when(usuarioService.toUsuarioResponseDTO(any())).thenCallRealMethod();

        // Act & Assert
        mockMvc.perform(get("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario obtenido exitosamente"))
                .andExpect(jsonPath("$.data.email").value("juan@example.com"));

        verify(usuarioService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "juan@example.com")
    void testGetUsuarioByIdNoEncontrado() throws Exception {
        // Arrange
        when(usuarioService.getUserById(999L))
                .thenThrow(new Exception("Usuario no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/usuarios/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    @WithMockUser(username = "juan@example.com")
    void testUpdateUsuario() throws Exception {
        // Arrange
        when(usuarioService.updateUser(eq(1L), any(UsuarioRequest.class))).thenReturn(usuario);
        when(usuarioService.toUsuarioResponseDTO(any())).thenCallRealMethod();

        // Act & Assert
        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario actualizado exitosamente"));

        verify(usuarioService, times(1)).updateUser(eq(1L), any(UsuarioRequest.class));
    }

    @Test
    @WithMockUser(username = "juan@example.com")
    void testDeleteUsuario() throws Exception {
        // Arrange
        doNothing().when(usuarioService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado exitosamente"));

        verify(usuarioService, times(1)).deleteUser(1L);
    }

    @Test
    void testAccesoSinToken() throws Exception {
        // Act & Assert - Sin token debe retornar 401
        mockMvc.perform(get("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}




