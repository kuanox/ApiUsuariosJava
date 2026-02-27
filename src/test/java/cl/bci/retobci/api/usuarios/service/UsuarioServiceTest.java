package cl.bci.retobci.api.usuarios.service;

import cl.bci.retobci.api.usuarios.config.JwtService;
import cl.bci.retobci.api.usuarios.dto.RegistroUsuarioDTO;
import cl.bci.retobci.api.usuarios.dto.TelefonoDTO;
import cl.bci.retobci.api.usuarios.dto.UsuarioRequest;
import cl.bci.retobci.api.usuarios.model.Usuario;
import cl.bci.retobci.api.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private RegistroUsuarioDTO registroDTO;
    private Usuario usuario;
    private TelefonoDTO telefonoDTO;

    @BeforeEach
    void setUp() {
        // Configurar propiedades usando ReflectionTestUtils
        ReflectionTestUtils.setField(usuarioService, "emailPattern", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        ReflectionTestUtils.setField(usuarioService, "passwordPattern", "^[a-zA-Z0-9]{6,}$");

        // Preparar datos de prueba
        telefonoDTO = TelefonoDTO.builder()
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
    }

    @Test
    void testRegisterUsuarioExitoso() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");
        when(jwtService.generateToken(any())).thenReturn("token123");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.register(registroDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getName());
        assertEquals("juan@example.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void testRegisterEmailDuplicado() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.register(registroDTO));
        assertEquals("El correo ya está registrado", exception.getMessage());
    }

    @Test
    void testRegisterEmailInvalido() throws Exception {
        // Arrange
        registroDTO.setEmail("emailinvalido");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.register(registroDTO));
        assertEquals("El formato del correo no es válido", exception.getMessage());
    }

    @Test
    void testRegisterContraseñaDebil() throws Exception {
        // Arrange
        registroDTO.setPassword("weak");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.register(registroDTO));
        assertEquals("La contraseña no cumple con los requisitos", exception.getMessage());
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.getUserById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getName());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNoEncontrado() throws Exception {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.getUserById(1L));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testGetAllUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        // Act
        List<Usuario> resultado = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUsuario() throws Exception {
        // Arrange
        UsuarioRequest updateRequest = UsuarioRequest.builder()
                .name("Juan Carlos")
                .email("juan.carlos@example.com")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("juan.carlos@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void testUpdateUsuarioConEmailDuplicado() throws Exception {
        // Arrange
        UsuarioRequest updateRequest = UsuarioRequest.builder()
                .email("otro@example.com")
                .build();

        Usuario otroUsuario = Usuario.builder()
                .id(2L)
                .email("otro@example.com")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("otro@example.com")).thenReturn(Optional.of(otroUsuario));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.updateUser(1L, updateRequest));
        assertEquals("El correo ya está registrado", exception.getMessage());
    }

    @Test
    void testDeleteUsuario() throws Exception {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.deleteUser(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUsuarioNoEncontrado() throws Exception {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.deleteUser(1L));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testLogin() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Password123", "Password123")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("token123");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.login("juan@example.com", "Password123");

        // Assert
        assertNotNull(resultado);
        assertEquals("juan@example.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void testLoginContraseñaIncorrecta() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpassword", "Password123")).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> usuarioService.login("juan@example.com", "wrongpassword"));
        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }
}



