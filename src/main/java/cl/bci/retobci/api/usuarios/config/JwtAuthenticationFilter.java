package cl.bci.retobci.api.usuarios.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Endpoints públicos que NO requieren validación de JWT
        String requestPath = request.getRequestURI();
        if (requestPath.contains("/api/v1/auth/register") || requestPath.contains("/api/v1/auth/login")) {
            // Estos endpoints son públicos, continuar sin validar JWT
            filterChain.doFilter(request, response);
            return;
        }

        // Si no hay header Authorization o no comienza con "Bearer ", continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token (después de "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // Intentar extraer el email del token
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // Cargar detalles del usuario
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    // Validar que el token sea válido
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Token válido: crear el token de autenticación
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        // Token inválido o expirado
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"mensaje\": \"Token JWT inválido o expirado\"}");
                        return;
                    }
                } catch (UsernameNotFoundException e) {
                    // Usuario no encontrado en BD
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"mensaje\": \"Token JWT inválido - Usuario no encontrado\"}");
                    return;
                }
            }
        } catch (JwtException e) {
            // Token mal formado, firma inválida, etc.
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Traducir el mensaje de error del JWT a español
            String mensajeError = traducirErrorJwt(e.getMessage());
            response.getWriter().write("{\"mensaje\": \"Token JWT inválido: " + mensajeError + "\"}");
            return;
        } catch (IllegalArgumentException e) {
            // Token vacío o nulo
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\": \"Token JWT no proporcionado o vacío\"}");
            return;
        } catch (Exception e) {
            // Cualquier otra excepción
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\": \"Error validando token JWT: " + e.getMessage() + "\"}");
            return;
        }

        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    /**
     * Traduce mensajes de error de JWT de inglés a español
     */
    private String traducirErrorJwt(String mensajeOriginal) {
        if (mensajeOriginal == null) {
            return "Token JWT inválido";
        }

        // Traducir mensajes comunes de JWT
        if (mensajeOriginal.contains("JWT signature does not match")) {
            return "La firma del token JWT no coincide - Token no válido";
        }
        if (mensajeOriginal.contains("Claims is not yet valid")) {
            return "El token aún no es válido - Fecha de validez futura";
        }
        if (mensajeOriginal.contains("Claims has expired")) {
            return "El token ha expirado";
        }
        if (mensajeOriginal.contains("JWT was not recognized")) {
            return "El formato del token JWT no es reconocido";
        }
        if (mensajeOriginal.contains("signed claims JWT used for this instance")) {
            return "El token JWT no está firmado correctamente";
        }
        if (mensajeOriginal.contains("Malformed")) {
            return "El token JWT tiene un formato incorrecto";
        }
        if (mensajeOriginal.contains("Invalid")) {
            return "El token JWT es inválido";
        }
        if (mensajeOriginal.contains("Expired")) {
            return "El token JWT ha expirado";
        }
        if (mensajeOriginal.contains("signature")) {
            return "Error en la firma del token JWT";
        }

        // Si no hay coincidencia, retornar el mensaje original
        return mensajeOriginal;
    }

}
