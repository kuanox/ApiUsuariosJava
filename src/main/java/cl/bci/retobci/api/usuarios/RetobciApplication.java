package cl.bci.retobci.api.usuarios;

import cl.bci.retobci.api.usuarios.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {
		"cl.bci.retobci.api.usuarios"},
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class}))
public class RetobciApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetobciApplication.class, args);
	}

}
