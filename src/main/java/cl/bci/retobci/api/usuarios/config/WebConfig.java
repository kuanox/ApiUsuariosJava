package cl.bci.retobci.api.usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5000") // Permite solicitudes desde este origen
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                    .allowedHeaders("*") // Permite cualquier encabezado
                    .allowCredentials(true) // Si quieres permitir el envío de cookies o credenciales
                    .maxAge(3600); // Cachea la configuración CORS durante 1 hora (3600 segundos)
        }
}
