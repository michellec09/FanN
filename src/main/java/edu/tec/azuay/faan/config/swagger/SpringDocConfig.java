package edu.tec.azuay.faan.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration

@OpenAPIDefinition(
        info = @Info(
                title = "Family Lost - API",
                version = "1.0",
                description = "API for the FAAN project",
                contact = @Contact(
                        name = "FAAN",
                        url = "https://www.faanecuador.org/es/"
                )
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "https://fannok-production.up.railway.app/api/v1",
                        description = "Production server"
                ),
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "http://localhost:8080/api/v1",
                        description = "Local server"
                )
        }
)

@SecurityScheme(
        name = "Security FAAN API",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)

public class SpringDocConfig {
}
