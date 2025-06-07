package doldol_server.doldol.common.config;

import static doldol_server.doldol.common.constants.CorsConstant.ALLOWED_CREDENTIALS;
import static doldol_server.doldol.common.constants.CorsConstant.ALLOWED_HEADERS;
import static doldol_server.doldol.common.constants.CorsConstant.ALLOWED_METHODS;
import static doldol_server.doldol.common.constants.CorsConstant.ALLOWED_ORIGINS;
import static doldol_server.doldol.common.constants.CorsConstant.MAX_AGE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowCredentials(ALLOWED_CREDENTIALS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setMaxAge(MAX_AGE);
        configuration.addExposedHeader(HttpHeaders.SET_COOKIE);
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
