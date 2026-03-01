package cl.bci.retobci.api.usuarios.service;

import cl.bci.retobci.api.usuarios.config.JwtService;
import cl.bci.retobci.api.usuarios.dto.PhoneRequest;
import cl.bci.retobci.api.usuarios.dto.RegistroUsuarioDTO;
import cl.bci.retobci.api.usuarios.dto.UsuarioRequest;
import cl.bci.retobci.api.usuarios.dto.UsuarioResponseDTO;
import cl.bci.retobci.api.usuarios.model.Phone;
import cl.bci.retobci.api.usuarios.model.Usuario;
import cl.bci.retobci.api.usuarios.repository.PhonesRepository;
import cl.bci.retobci.api.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Data
@Validated
public class UsuarioService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.password.pattern}")
    private String passwordPattern;

    @Value("${user.email.pattern}")
    private String emailPattern;

    private final UsuarioRepository usuarioRepository;
    private final PhonesRepository phonesRepository;

    public UsuarioService(JwtService jwtService, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, PhonesRepository phonesRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.phonesRepository = phonesRepository;
    }

    @Transactional
    public Usuario register(RegistroUsuarioDTO registroUsuarioDTO) throws Exception {

        // Validamos que el email tenga un formato válido usando Pattern
        Pattern emailRegex = Pattern.compile(emailPattern);
        if (!emailRegex.matcher(registroUsuarioDTO.getEmail()).matches()) {
            throw new Exception("El formato del correo no es válido");
        }

        // Validamos si el correo ya está registrado
        if (usuarioRepository.findByEmail(registroUsuarioDTO.getEmail()).isPresent()) {
            throw new Exception("El correo ya está registrado");
        }

        // Validamos el formato de la contraseña con la expresión regular de la property
        if (!registroUsuarioDTO.getPassword().matches(passwordPattern)) {
            throw new Exception("La contraseña no cumple con los requisitos");
        }

        // Creamos el nuevo usuario con la contraseña encriptada
        var usuario = Usuario.builder()
                .name( registroUsuarioDTO.getName() )
                .email( registroUsuarioDTO.getEmail() )
                .password( passwordEncoder.encode(registroUsuarioDTO.getPassword()) )
                .created( LocalDateTime.now() )
                .modified( LocalDateTime.now() )
                .lastLogin( LocalDateTime.now() )
                .isActive(true)
                .build();

        // Convertir lista de RegistroUsuarioDTO Phones a lista de Phone
        registroUsuarioDTO.getPhones().stream()
                .map(phoneRequest -> {
                    // Aquí va el mapeo del teléfono
                    Phone phone = new Phone();
                    phone.setNumber(phoneRequest.getNumber());
                    phone.setCitycode(phoneRequest.getCitycode());
                    phone.setCountrycode(phoneRequest.getContrycode());
                    return phone;
                })
                .filter(Objects::nonNull) // Filtra los valores null que puedan haber aparecido
                .forEach(usuario::addPhone);

        // Se Generamos un token
        var token = jwtService.generateToken(usuario);
        usuario.setToken(token);

        // Guardamos el usuario en la base de datos H2
        return usuarioRepository.save(usuario);
    }

    public Usuario getUserById(Long id) throws Exception {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario loadUserByEmail(String email) throws Exception {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
    }

    public Usuario updateUser(Long id, UsuarioRequest usuarioRequest) throws Exception {
        Usuario usuario = getUserById(id);

        if (usuarioRequest.getName() != null) {
            usuario.setName(usuarioRequest.getName());
        }
        if (usuarioRequest.getEmail() != null) {
            // validar formato de email usando la property
            Pattern emailRegex = Pattern.compile(emailPattern);
            if (!emailRegex.matcher(usuarioRequest.getEmail()).matches()) {
                throw new Exception("El formato del correo no es válido");
            }
            // Verificar que el email no esté siendo usado por otro usuario
            if (!usuarioRequest.getEmail().equals(usuario.getEmail())) {
                if (usuarioRepository.findByEmail(usuarioRequest.getEmail()).isPresent()) {
                    throw new Exception("El correo ya está registrado");
                }
            }
            usuario.setEmail(usuarioRequest.getEmail());
        }
        if (usuarioRequest.getPassword() != null) {
            String pw = usuarioRequest.getPassword().trim();
            // Si la contraseña parece ya estar en formato BCrypt, no intentar validarla ni re-encriptarla
            if (pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$")) {
                // Si es igual a la almacenada, no hacer nada; si es distinto, asumimos que es un hash válido enviado por el cliente
                if (!pw.equals(usuario.getPassword())) {
                    usuario.setPassword(pw);
                }
            } else {
                // Validar y encriptar la contraseña de texto plano
                if (!pw.matches(passwordPattern)) {
                    throw new Exception("La contraseña no cumple con los requisitos");
                }
                usuario.setPassword(passwordEncoder.encode(pw));
            }
        }
        if (usuarioRequest.getPhones() != null) {
            // Asegurar que la lista de phones exista antes de clear
            if (usuario.getPhones() == null) {
                usuario.setPhones(new ArrayList<>());
            }
            // Limpiar los phones existentes para evitar conflictos con cascade
            usuario.getPhones().clear();

            // Convertir PhoneRequest a Phone antes de asignar
            List<Phone> updatedPhones = usuarioRequest.getPhones().stream()
                    .map(phoneRequest -> {
                        Phone phone = new Phone();
                        phone.setId(phoneRequest.getId()); // Si el ID es proporcionado, lo asignamos; si no, se generará automáticamente
                        phone.setNumber(phoneRequest.getNumber());
                        phone.setCitycode(phoneRequest.getCitycode());
                        phone.setCountrycode(phoneRequest.getContrycode());
                        // Asegúrate de que si es necesario, también asignas el usuario a cada teléfono
                        phone.setUsuario(usuario);  // si la relación bidireccional está configurada
                        return phone;
                    })
                    .collect(Collectors.toList());

            usuario.getPhones().addAll(updatedPhones);

        }

        usuario.setModified(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public void deleteUser(Long id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new Exception("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        // Aseguramos que no haya NullPointerException
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .name(usuario.getName())
                .email(usuario.getEmail())
                .isActive(usuario.isActive())
                .phones(usuario.getPhones() != null ? usuario.getPhones().stream()
                        .map(phone -> PhoneRequest.builder()
                                .id(phone.getId())
                                .number(phone.getNumber() != null ? phone.getNumber() : "")
                                .citycode(phone.getCitycode() != null ? phone.getCitycode() : "")
                                .contrycode(phone.getCountrycode() != null ? phone.getCountrycode() : "")
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())  // Manejo de null para phones
                .created(usuario.getCreated())
                .modified(usuario.getModified())
                .lastLogin(usuario.getLastLogin())
                .build();
    }

    public Usuario login(String email, String password) throws Exception {
        // Validar que el email sea válido
        Pattern emailRegex = Pattern.compile(emailPattern);
        if (!emailRegex.matcher(email).matches()) {
            throw new Exception("El formato del correo no es válido");
        }

        // Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario o contraseña incorrectos"));

        // Validar que el usuario esté activo
        if (!usuario.isActive()) {
            throw new Exception("El usuario está inactivo");
        }

        // Validar la contraseña (comparar con la contraseña encriptada)
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new Exception("Usuario o contraseña incorrectos");
        }

        // Actualizar lastLogin
        usuario.setLastLogin(LocalDateTime.now());

        // Generar un nuevo token
        String token = jwtService.generateToken(usuario);
        usuario.setToken(token);

        // Guardar el usuario con el nuevo token y lastLogin actualizado
        return usuarioRepository.save(usuario);
    }
}
